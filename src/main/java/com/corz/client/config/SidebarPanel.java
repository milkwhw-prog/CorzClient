package com.corz.client.config;

import com.corz.client.CorzClient;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.TwoDidUtils;
import com.corz.client.gui.VertexGuiUtils;
import com.corz.client.inventory.DrawMatricesUtils;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixClearUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixIdentifierUtils;
import com.corz.client.render.MatrixIdentifierUtils.Spring;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.MatrixStackUtilsAmount.Anims;
import com.corz.client.render.MatrixStackUtilsAmount.Hits;
import com.corz.client.render.RenderMatrixUtilsRelease;
import com.corz.client.render.VertexTextureFont;
import com.corz.client.render.VertexTexturePeek;
import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import com.corz.client.categories.misc.NameProtect;
import com.corz.client.categories.settings.ClientSounds;
import com.corz.client.categories.settings.License;
import ai.auth.LicenseRuntime;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.PngIcons;

public class SidebarPanel {
   private final int panelY;
   private float entranceScale;
   private static final String PROFILE_ANIM_KEY = "profile";
   private final Spring highlightSpring;
   private final String[] badgeTexts;
   private final Spring collapseSpring;
   private final Anims rowAnims;
   private final int rowCount;
   private float profile;
   private boolean highlightInitialized;
   private final int indicatorRadius;
   private float animatedTooltipY;
   private final int headerDividerYExpanded;
   private boolean badgesInitialized;
   private final int profileDividerYExpanded;
   private float iconScale;
   private int tooltipIndex;
   private final int[] sectionLabelYs;
   private static boolean justCreated;
   private static final String VERSION_TEXT = "v2.0.1";
   private final List<IconsPngType> categories;
   private final float[] tooltipWidths;
   private final int finalStaggerSlot;
   private final int panelHeight;
   private float entranceAlpha;
   private final int panelX;
   private String accountInfoRaw;
   private int lastHoveredIndex;
   private final float[] activeRowAmounts;
   private final Runnable[] actions;
   private final float[] itemHoverAmounts;
   private final float[] badgeWidths;
   private final Hits hitRegions;
   private static int lastScreenHash;
   private final Module[][] categoryModules;
   private float revealProgress;
   private final int[] rowYs;
   private float profileHoverAmount;
   private final float headerDividerYCollapsed;
   private final Runnable toggleCollapseAction = this::toggleCollapsed;
   private boolean interactive;
   private boolean hitsEnabled;
   private final int[] lastEnabledCounts;
   private final int expandedWidth;
   private final int rowHeight;
   private static final String LOGO_ANIM_KEY = "logo";
   private String accountInfoFitted;
   private int currentWidth;
   private String usernameRaw;
   private float tooltipAlpha;
   private int hoveredIndex;
   private float highlightY;
   private float introElapsedMs;
   private static final String EXPAND_LABEL = "Expand";
   private float logoHoverAmount;
   private final int halfRowGap;
   private final float[] badgeAlphas;
   private String usernameFitted;
   private final String[] badgeAnimIds;
   private static long introStartNanos;
   private final int profileCardY;
   private float introFade;
   private final float[] sectionSeparatorYs;
   private final boolean[] startsSection;
   private boolean hoverEnabled;
   private static long lastFrameNanos;
   private int pendingTooltipIndex;
   private final float profileDividerYCollapsed;
   private final int[] staggerSlots;
   private long lastTooltipNanoTime;
   private static final Identifier LOGO_TEXTURE = Identifier.of("corzclient", "textures/corz_mark.png");
   private final Runnable profileAction;
   private final int rowPitch;
   private int lastTooltipIndex;
   private final String[] rowAnimIds;

   private void renderActiveRowHighlight(DrawContext context, int index) {
      if (index >= 0) {
         this.applyStaggerForSlot(this.staggerSlots[index]);
         if (!(this.entranceAlpha <= 0.002F)) {
            float logoProfile = this.getHighlightX();
            float logoProfileValue = this.panelX + this.currentWidth;
            float matrixStackValue = this.highlightY;
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, logoProfileValue, matrixStackValue + this.rowHeight / 2.0F, this.entranceScale);
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.entranceAlpha);

            try {
               this.renderHighlightFrame(logoProfile, matrixStackValue, logoProfileValue, matrixStackValue + this.rowHeight, this.getHighlightCornerSize());
               float logoProfileLimit = 1.0F - this.revealProgress;
               if (logoProfileLimit > 0.004F) {
                  float round = Math.round(this.rowHeight / 2.0F * this.iconScale);
                  RenderMatrixUtilsRelease.render(context, logoProfile + 3.0F, matrixStackValue + (this.rowHeight - round) / 2.0F, round, (double)logoProfileLimit);
               }
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            }
         }
      }
   }

   private static boolean isGeneralCategory(IconsPngType iconsPngType) {
      return iconsPngType == IconsPngType.SETTING || iconsPngType == IconsPngType.CONFIGS || iconsPngType == IconsPngType.THEME || iconsPngType == IconsPngType.SOCIALS;
   }

   private float getRowIconX() {
      return this.panelX + 16 + -4.0F * this.profile;
   }

   public void updateCollapseAnimation() {
      double logoProfileValue = PngIcons.getInstance().isBooleanValue() ? 1.0 : 0.0;
      if (this.collapseSpring.springPosition != logoProfileValue || this.collapseSpring.anywayEnableCount != 0.0) {
         double logoProfileValue2 = MatrixClearUtils.getAmount();
         int logoProfileDivisor = Math.max(1, (int)Math.ceil(logoProfileValue2 / 0.004166666666666667));
         double logoProfileValue3 = logoProfileValue2 / logoProfileDivisor;

         for (int i = 0; i < logoProfileDivisor; i++) {
            this.collapseSpring.handleAmount(logoProfileValue, 576.0, 48.0, logoProfileValue3);
         }

         if (Math.abs(this.collapseSpring.springPosition - logoProfileValue) < 0.001 && Math.abs(this.collapseSpring.anywayEnableCount) < 0.02) {
            this.collapseSpring.handleAmount(logoProfileValue);
         }
      }

      this.updateCollapseDerivedValues();
   }

   private static int applyAlpha(int logoProfileValue, double logoProfileValue2) {
      return Math.max(0, Math.min(255, (int)Math.round(logoProfileValue2 * 255.0))) << 24 | logoProfileValue & 16777215;
   }

   private static int getAccentColor() {
      return TwoDidUtils.getValue(CardMatrixUtils.getValue(CardMatrixUtils.getValue9(), MatrixEspUtils.releaseRightValue, 0.08));
   }

   public void renderPanel(DrawContext context, int logoProfileValue, int heightValue, float modulesClearValue) {
      if (this.interactive) {
         VertexTextureFont fontRenderer = CorzClient.fontRenderer();
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         double logoProfileValue4 = MatrixClearUtils.getAmount();
         this.hitRegions.update();
         this.updateIntroClock();
         int indexOf = this.categories.indexOf(PngIcons.getInstance().getIconsPngType());
         this.updateHighlightSpring(indexOf, logoProfileValue4);
         this.hoveredIndex = -1;
         boolean logoProfileFlag = this.profile > 5.0E-4F && this.profile < 0.9995F;
         if (logoProfileFlag) {
            context.enableScissor(this.panelX, this.panelY, this.panelX + this.currentWidth, this.panelY + this.panelHeight);
         }

         try {
            this.renderPanelBackground(context, indexOf);
            if (!CardMatrixUtils.isModernFlag()) {
               DrawMatricesUtils.renderGradientFill(context, this.panelX + 2, this.panelY + 2, this.panelX + this.currentWidth - 2, this.panelY + 18, 1073741824, 0);
            }

            TwoDidUtils.renderMinPeek(context, 1, this.panelX, this.panelY, this.currentWidth, this.panelHeight, 7.0F, 1.0F);
            this.renderLogoHeader(context, matrixStack, logoProfileValue, heightValue, logoProfileValue4);
            this.renderDivider(context, this.headerDividerYExpanded + (this.headerDividerYCollapsed - this.headerDividerYExpanded) * this.profile, 1, 1.0F);
            this.updateBadges(logoProfileValue4);
            this.renderActiveRowHighlight(context, indexOf);

            for (int i = 0; i < this.rowCount; i++) {
               this.renderCategoryRow(context, fontRenderer, matrixStack, i, indexOf, logoProfileValue, heightValue, logoProfileValue4);
            }

            this.renderDivider(context, this.profileDividerYExpanded + (this.profileDividerYCollapsed - this.profileDividerYExpanded) * this.profile, this.finalStaggerSlot, 1.0F);
            this.renderProfileCard(context, fontRenderer, matrixStack, logoProfileValue, heightValue, logoProfileValue4);
         } finally {
            if (logoProfileFlag) {
               context.disableScissor();
            }
         }

         this.renderActiveRowEdgeStrip(indexOf);
         this.pendingTooltipIndex = this.profile > 0.98F ? this.hoveredIndex : -1;
         if (this.hoveredIndex >= 0 && this.hoveredIndex != this.lastHoveredIndex) {
            ClientSounds.updateScrollHover();
         }

         this.lastHoveredIndex = this.hoveredIndex;
      }
   }

   private void renderCollapsedBadgeDot(DrawContext context, int index, float releaseValue, float y, float releaseValue3, float cardMatrixFactor) {
      float panelOverlayLimit = this.badgeAlphas[index] * this.revealProgress;
      if (!(panelOverlayLimit <= 0.01F) && this.badgeTexts[index] != null) {
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, releaseValue, y, this.rowAnims.getTime(this.badgeAnimIds[index], 0.0F));
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            if (!CardMatrixUtils.isModernFlag()) {
               int releaseValue6 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-15921132, -1, 0.045 * releaseValue3), getAccentColor(), cardMatrixFactor);
               float corzLicenseLimit = 3.25F;
               MatrixPeekUtilsRender.renderMatrixPeek(context, releaseValue - corzLicenseLimit, y - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, 0xFF000000 | releaseValue6 & 16777215);
            }

            MatrixPeekUtilsRender.renderMatrixPeek(context, releaseValue - 2.25F, y - 2.25F, 4.5F, 4.5F, 2.25F, 0xFF000000 | MatrixEspUtils.releaseRightValue);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }
      }
   }

   private float getTooltipTargetY(int index) {
      if (index < this.rowCount) {
         return this.rowYs[index] + this.rowHeight / 2.0F - 8.0F;
      } else {
         return index == this.rowCount ? this.profileCardY + 17.0F - 8.0F : this.panelY + 15 + 11.0F - 8.0F;
      }
   }

   private void renderAccountTexts(VertexTextureFont vertexTextureFont, float logoValue, float profileAmount) {
      float logoValueOffset = this.panelX + 45;
      String text = LicenseRuntime.getLicenseOwnerLabel();
      if (text == null) {
         text = "";
      }

      if (!text.equals(this.accountInfoRaw)) {
         this.accountInfoRaw = text;
         this.accountInfoFitted = fitAccountText(vertexTextureFont, text, this.getTextMaxWidth() / 0.82F);
      }

      float logoValue3 = logoValue + 17.0F - 8.97F;
      MatrixStackUtilsAmount.handle(this.usernameFitted, logoValueOffset, MatrixStackUtilsAmount.getAmount(logoValue3 + 6.44F - 11.5F), -920587);
      MatrixStackUtilsAmount.handlePushScale(
         this.accountInfoFitted,
         logoValueOffset + 0.5F,
         MatrixStackUtilsAmount.getAmount(logoValue3 + 6.44F + 11.5F - 9.43F),
         0.82F,
         CardMatrixUtils.getValue(-11117467, -7630437, 0.4 + 0.6 * profileAmount)
      );
   }

   private void renderDivider(DrawContext context, float logoProfileValue, int logoProfileValue2, float logoProfileFactor) {
      this.applyStaggerForSlot(logoProfileValue2);
      float matrixStackFactor = this.entranceAlpha * logoProfileFactor;
      if (!(matrixStackFactor <= 0.002F)) {
         float logoProfileValue4 = 16.0F + -7.0F * this.profile;
         MatrixPeekUtilsRender.renderMatrixPeek(
            context,
            this.panelX + logoProfileValue4,
            MatrixStackUtilsAmount.getAmount(logoProfileValue),
            this.currentWidth - logoProfileValue4 * 2.0F,
            MatrixPeekUtilsRender.getAmount(),
            MatrixStackUtilsAmount.getValue(452984831, matrixStackFactor)
         );
      }
   }

   private void renderProfileCard(DrawContext context, VertexTextureFont vertexTextureFont, MatrixStack matrices, int profileLogoValue, int scaledY, double matrixIdentifierFactor) {
      this.applyStaggerForSlot(this.finalStaggerSlot);
      float logoValueOffset = this.panelX + 8;
      float materialFactor = this.currentWidth - 16;
      float logoValue = this.profileCardY;
      float profileLogoValue5 = 5.0F;
      float textureRoundedValue = logoValueOffset + profileLogoValue5 + ((this.currentWidth - 24) / 2.0F - 8.0F - profileLogoValue5) * this.profile;
      float randomValue = logoValue + profileLogoValue5;
      boolean profileLogoFlag = this.profile >= 0.5F;
      float profileLogoValue8 = profileLogoFlag ? textureRoundedValue - 2.0F : logoValueOffset;
      float matrixStackValue = profileLogoFlag ? randomValue - 2.0F : logoValue;
      float identifierText = profileLogoFlag ? 28.0F : materialFactor;
      float segmentSlider = profileLogoFlag ? 28.0F : 34.0F;
      boolean profileLogoFlag2 = this.profileAction != null && this.hoverEnabled && MatrixStackUtilsAmount.canFlag(profileLogoValue, scaledY, profileLogoValue8, matrixStackValue, identifierText, segmentSlider);
      if (profileLogoFlag2) {
         this.hoveredIndex = this.rowCount;
      }

      this.profileHoverAmount = animateToward(this.profileHoverAmount, profileLogoFlag2 ? 1.0F : 0.0F, profileLogoFlag2 ? 16.0 : 11.0, matrixIdentifierFactor, 0.002F);
      String expandText = NameProtect.getString(Module.mc.getSession().getUsername());
      if (!expandText.equals(this.usernameRaw)) {
         this.usernameRaw = expandText;
         this.usernameFitted = vertexTextureFont.formatText(expandText, this.getTextMaxWidth());
         this.tooltipWidths[this.rowCount] = 0.0F;
      }

      if (!(this.entranceAlpha <= 0.002F)) {
         float profileAmount = this.profileHoverAmount;
         float profileLogoLimit = 1.0F - smoothStep(this.profile / 0.6F);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
            context, logoValueOffset + materialFactor / 2.0F, logoValue + 17.0F, this.entranceScale * this.rowAnims.getTime("profile", 0.025F + 0.045F * this.profile)
         );
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.entranceAlpha);

         try {
            int profileLogoValue12 = CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), -1, 0.05 * profileAmount);
            if (profileLogoLimit > 0.002F) {
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(profileLogoLimit);

               try {
                  MatrixPeekUtilsRender.render(context, logoValueOffset, logoValue, materialFactor, 34.0F, 7.0F, profileLogoValue12, CardMatrixUtils.getValue(352321535, 1040187391, 0.6 * profileAmount));
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }

            float matrixStackFactor = profileAmount * this.revealProgress;
            if (matrixStackFactor > 0.01F) {
               MatrixPeekUtilsRender.renderMatrixPeek(
                  context, textureRoundedValue - 2.0F, randomValue - 2.0F, 28.0F, 28.0F, 7.0F, MatrixPeekUtilsRender.getAmount(), MatrixStackUtilsAmount.getValue(1040187391, matrixStackFactor)
               );
            }

            if (Module.mc.player != null) {
               Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
               Identifier texturePath = Module.mc.player.getSkin().body().texturePath();
               CorzRoundedUtils.handleTextureRounded(positionMatrix, textureRoundedValue, randomValue, textureRoundedValue + 24.0F, randomValue + 24.0F, 0.125F, 0.125F, 0.25F, 0.25F, texturePath, 5.0F);
               CorzRoundedUtils.handleTextureRounded(positionMatrix, textureRoundedValue, randomValue, textureRoundedValue + 24.0F, randomValue + 24.0F, 0.625F, 0.125F, 0.75F, 0.25F, texturePath, 5.0F);
            } else {
               MatrixStackUtilsAmount.renderReleaseRight(context, expandText, textureRoundedValue, randomValue, 24.0F);
            }

            if (this.introFade > 0.002F) {
               float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(this.introFade);

               try {
                  this.renderAccountTexts(vertexTextureFont, logoValue, profileAmount);
                  if (profileAmount > 0.01F) {
                     MatrixPeekUtilsIdentifier.handle(
                        logoValueOffset + materialFactor - 13.5F - (1.0F - profileAmount) * 2.0F,
                        logoValue + 13.0F,
                        8.0F,
                        8.0F,
                        IcoGenUtils.getChevronIco(),
                        new Color(MatrixStackUtilsAmount.getValue(-5854026, profileAmount), true),
                        matrices
                     );
                  }
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
               }
            }

            int logoProfileValue = 3854714;
            float profileLogoValue14 = textureRoundedValue + 24.0F - 2.0F;
            float profileLogoValue15 = randomValue + 24.0F - 2.0F;
            if (!CardMatrixUtils.isModernFlag()) {
               int profileLogoValue16 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-15723751, -1, 0.05 * profileAmount), -15921132, this.profile);
               MatrixPeekUtilsRender.renderMatrixPeek(context, profileLogoValue14 - 4.0F, profileLogoValue15 - 4.0F, 8.0F, 8.0F, 4.0F, 0xFF000000 | profileLogoValue16 & 16777215);
            }

            MatrixPeekUtilsRender.renderMatrixPeek(context, profileLogoValue14 - 3.0F, profileLogoValue15 - 3.0F, 6.0F, 6.0F, 3.0F, applyAlpha(logoProfileValue, 0.38));
            MatrixPeekUtilsRender.renderMatrixPeek(context, profileLogoValue14 - 2.0F, profileLogoValue15 - 2.0F, 4.0F, 4.0F, 2.0F, 0xFF000000 | logoProfileValue);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }

         if (this.profileAction != null && this.hitsEnabled) {
            this.hitRegions.handleFlag(profileLogoValue8, matrixStackValue, identifierText, segmentSlider, this.profileAction, this.rowAnims, "profile");
         }
      }
   }

   private void updateIntroClock() {
      long nanoTime = System.nanoTime();
      if (justCreated || lastFrameNanos == 0L || nanoTime - lastFrameNanos > 300000000L) {
         introStartNanos = nanoTime;
         justCreated = false;
      }

      lastFrameNanos = nanoTime;
      this.introElapsedMs = introStartNanos == 0L ? 0.0F : (float)(nanoTime - introStartNanos) / 1000000.0F;
      if (introStartNanos != 0L && this.introElapsedMs > this.finalStaggerSlot * 20.0F + 200.0F) {
         introStartNanos = 0L;
      }
   }

   private void renderPanelBackground(DrawContext context, int index) {
      boolean logoRoundFlag = false;
      if (index >= 0) {
         this.applyStaggerForSlot(this.staggerSlots[index]);
         logoRoundFlag = this.entranceAlpha > 0.5F;
      }

      if (!logoRoundFlag) {
         if (CardMatrixUtils.isModernFlag()) {
            MatrixPeekUtilsRender.renderMatrixPeek(context, this.panelX, this.panelY, this.currentWidth, this.panelHeight, 7.0F, CardMatrixUtils.getValue8());
            MatrixPeekUtilsRender.renderMatrixPeek(
               context, this.panelX, this.panelY, this.currentWidth, this.panelHeight, 7.0F, MatrixPeekUtilsRender.getAmount(), 352321535
            );
         } else {
            MatrixPeekUtilsRender.render(context, this.panelX, this.panelY, this.currentWidth, this.panelHeight, 7, CardMatrixUtils.getValue8(), 352321535);
         }
      } else {
         MatrixPeekUtilsRender.renderMatrixPeek(context, this.panelX, this.panelY, this.currentWidth, this.panelHeight, 7.0F, CardMatrixUtils.getValue8());
         float logoRoundValue = MatrixPeekUtilsRender.getAmount();
         float logoRoundValue2 = this.panelX + this.currentWidth;
         float highlightCornerSize = this.getHighlightCornerSize();
         float logoRoundValue4 = this.highlightY - highlightCornerSize;
         float logoRoundValue5 = this.highlightY + this.rowHeight + highlightCornerSize;
         MatrixStackUtilsAmount.handlePeekPosition(
            this.panelX,
            this.panelY,
            this.currentWidth,
            this.panelHeight,
            7.0F,
            logoRoundValue,
            352321535,
            this.panelX - 1.0F,
            this.panelY - 1.0F,
            logoRoundValue2 - 7.0F,
            this.panelY + this.panelHeight + 1.0F
         );
         MatrixStackUtilsAmount.handlePeekPosition(
            this.panelX, this.panelY, this.currentWidth, this.panelHeight, 7.0F, logoRoundValue, 352321535, logoRoundValue2 - 7.0F, this.panelY - 1.0F, logoRoundValue2 + 1.0F, logoRoundValue4
         );
         MatrixStackUtilsAmount.handlePeekPosition(
            this.panelX, this.panelY, this.currentWidth, this.panelHeight, 7.0F, logoRoundValue, 352321535, logoRoundValue2 - 7.0F, logoRoundValue5, logoRoundValue2 + 1.0F, this.panelY + this.panelHeight + 1.0F
         );
      }
   }

   private void renderClientTitle(float corzValue, float y, float corzValue3) {
      VertexTexturePeek wave = VertexTexturePeek.getWave();
      float corzValue4;
      if (wave.isCorzclientFlag()) {
         float corzLimit = wave.getAmountForFloat(1.0F);
         float vertexTextureValue = corzLimit > 0.001F ? 15.4F / corzLimit : 14.0F;
         float corzValue6 = vertexTextureValue * 0.5F;
         float corzValue7 = 0.67F * corzValue6;
         float amountParsedCorzfontAmount = wave.getAmountParsedCorzfontAmount(vertexTextureValue);
         float corzValue9 = MatrixStackUtilsAmount.getAmount(y - (corzValue7 + 6.5F + corzValue3) / 2.0F + corzValue7 - amountParsedCorzfontAmount);
         wave.handleAmountPosition("Corz", MatrixStackUtilsAmount.getAmount(corzValue + 1.5F - 0.094F * corzValue6), corzValue9, -4205835, -1, vertexTextureValue);
         corzValue4 = corzValue9 + amountParsedCorzfontAmount - corzValue7;
      } else {
         float corzValue10 = 8.694F;
         float corzValue11 = 15.525001F;
         float priceValue = MatrixStackUtilsAmount.getAmount(y - (corzValue10 + 6.5F + corzValue3) / 2.0F + corzValue10 - corzValue11);
         MatrixStackUtilsAmount.handlePushScale("Corz", MatrixStackUtilsAmount.getAmount(corzValue + -0.52500004F), priceValue, 1.35F, -920587);
         corzValue4 = priceValue + corzValue11 - corzValue10;
      }

      MatrixStackUtilsAmount.handlePushScale("v2.0.1", corzValue + 0.5F, MatrixStackUtilsAmount.getAmount(y * 2.0F - corzValue4 - 9.43F), 0.82F, -11117467);
   }

   public void renderCollapsedTooltip(DrawContext context, boolean logoProfileFlag, float logoProfileBound) {
      if (this.interactive) {
         long nanoTime = System.nanoTime();
         double logoProfileValue = MatrixClearUtils.getAmount();
         int value = logoProfileFlag ? -1 : this.pendingTooltipIndex;
         if (value != this.lastTooltipIndex) {
            this.lastTooltipIndex = value;
            this.lastTooltipNanoTime = nanoTime;
         }

         if (value >= 0 && (this.tooltipAlpha > 0.3F || nanoTime - this.lastTooltipNanoTime >= 120000000L)) {
            if (this.tooltipIndex != value) {
               if (this.tooltipAlpha < 0.05F) {
                  this.animatedTooltipY = this.getTooltipTargetY(value);
               }

               this.tooltipIndex = value;
            }

            this.tooltipAlpha = animateToward(this.tooltipAlpha, 1.0F, 22.0, logoProfileValue, 0.004F);
         } else {
            this.tooltipAlpha = animateToward(this.tooltipAlpha, 0.0F, 18.0, logoProfileValue, 0.004F);
         }

         if (this.tooltipIndex >= 0 && !(this.tooltipAlpha <= 0.004F)) {
            this.animatedTooltipY = animateToward(this.animatedTooltipY, this.getTooltipTargetY(this.tooltipIndex), 30.0, logoProfileValue, 0.05F);
            String formatExpandText = this.getTooltipText(this.tooltipIndex);
            if (this.tooltipWidths[this.tooltipIndex] <= 0.0F) {
               this.tooltipWidths[this.tooltipIndex] = MatrixStackUtilsAmount.getAmount3(formatExpandText) + 14.0F;
            }

            float materialFactor = this.tooltipWidths[this.tooltipIndex];
            float matrixPeekValue = MatrixStackUtilsAmount.getAmount(Math.max(this.panelX + this.currentWidth + 7.0F, logoProfileBound) - 4.0F * (1.0F - this.tooltipAlpha));
            float matrixStackValue = MatrixStackUtilsAmount.getAmount(this.animatedTooltipY);
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.tooltipAlpha);

            try {
               MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, matrixPeekValue - 2.0F, matrixStackValue, materialFactor + 4.0F, 19.0F, 6.0F, -1946157056, 6.0F, 0.5F);
               MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, matrixStackValue, materialFactor, 16.0F, 5.0F, -65990120, 385875967);
               MatrixStackUtilsAmount.handle(formatExpandText, matrixPeekValue + 7.0F, MatrixStackUtilsAmount.getAmount(matrixStackValue, 16.0F, 1.0F), -920587);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      }
   }

   private static float smoothStep(float logoProfileFactor) {
      logoProfileFactor = MatrixStackUtilsAmount.getMax(logoProfileFactor);
      return logoProfileFactor * logoProfileFactor * (3.0F - 2.0F * logoProfileFactor);
   }

   private static void renderRoundedFrameSegments(float logoProfileValue, float matrixStackValue, float peekPositionLimit, float logoProfileValue4, float logoProfileValue5, float logoRoundValue, float logoProfileValue7, int releaseRight, float logoProfileValue9) {
      float logoProfileValue10 = peekPositionLimit - logoProfileValue9 - logoProfileValue7;
      float logoProfileValue11 = logoProfileValue9 + logoRoundValue;
      float loadedValue = peekPositionLimit - logoProfileValue - logoRoundValue + logoProfileValue5 + 2.0F;
      float segmentSlider = logoProfileValue4 - matrixStackValue - logoRoundValue * 2.0F;
      float max14 = Math.max(0.0F, logoProfileValue5 - logoRoundValue);
      MatrixStackUtilsAmount.handlePeekPosition(logoProfileValue + logoRoundValue, matrixStackValue + logoRoundValue, loadedValue, segmentSlider, max14, 0.0F, releaseRight, logoProfileValue + logoRoundValue - 1.0F, matrixStackValue + logoRoundValue - 1.0F, logoProfileValue10, logoProfileValue4 - logoRoundValue + 1.0F);
      MatrixStackUtilsAmount.handlePeekPosition(logoProfileValue + logoRoundValue, matrixStackValue + logoRoundValue, loadedValue, segmentSlider, max14, 0.0F, releaseRight, logoProfileValue10, matrixStackValue + logoRoundValue, peekPositionLimit, logoProfileValue4 - logoRoundValue);
      MatrixStackUtilsAmount.handlePeekPosition(
         logoProfileValue10 - logoProfileValue11, matrixStackValue - logoProfileValue9 - logoProfileValue11, logoProfileValue11 * 2.0F, logoProfileValue11 * 2.0F, logoProfileValue11, releaseRight, 1000000.0F, 1.0F, false, logoProfileValue10, matrixStackValue - logoProfileValue9, peekPositionLimit, matrixStackValue + logoRoundValue
      );
      MatrixStackUtilsAmount.handlePeekPosition(
         logoProfileValue10 - logoProfileValue11, logoProfileValue4 + logoProfileValue9 - logoProfileValue11, logoProfileValue11 * 2.0F, logoProfileValue11 * 2.0F, logoProfileValue11, releaseRight, 1000000.0F, 1.0F, false, logoProfileValue10, logoProfileValue4 - logoRoundValue, peekPositionLimit, logoProfileValue4 + logoProfileValue9
      );
   }

   private void renderHighlightFrame(float logoProfileValue, float matrixStackValue, float logoProfileValue3, float logoProfileValue4, float logoProfileValue5) {
      float logoRoundValue = MatrixPeekUtilsRender.getAmount();
      int releaseRight = getAccentColor();
      if (!CardMatrixUtils.isModernFlag() && releaseRight >>> 24 == 255) {
         renderRoundedFrameSegments(logoProfileValue, matrixStackValue, logoProfileValue3, logoProfileValue4, this.indicatorRadius, 0.0F, logoRoundValue, 352321535, logoProfileValue5);
         renderRoundedFrameSegments(logoProfileValue, matrixStackValue, logoProfileValue3, logoProfileValue4, this.indicatorRadius, logoRoundValue, logoRoundValue, releaseRight, logoProfileValue5);
      } else {
         renderRoundedFrameSegments(logoProfileValue, matrixStackValue, logoProfileValue3, logoProfileValue4, this.indicatorRadius, 0.0F, logoRoundValue, releaseRight, logoProfileValue5);
         float peekPositionLimit = logoProfileValue3 - logoProfileValue5 - logoRoundValue;
         float logoProfileValue8 = logoProfileValue5 + logoRoundValue;
         MatrixStackUtilsAmount.handlePeekPosition(
            logoProfileValue, matrixStackValue, logoProfileValue3 - logoProfileValue + this.indicatorRadius + 2.0F, logoProfileValue4 - matrixStackValue, this.indicatorRadius, logoRoundValue, 352321535, logoProfileValue - 1.0F, matrixStackValue - 1.0F, peekPositionLimit, logoProfileValue4 + 1.0F
         );
         MatrixStackUtilsAmount.handlePeekPosition(
            peekPositionLimit - logoProfileValue8, matrixStackValue - logoProfileValue5 - logoProfileValue8, logoProfileValue8 * 2.0F, logoProfileValue8 * 2.0F, logoProfileValue8, logoRoundValue, 352321535, peekPositionLimit, matrixStackValue - logoProfileValue5, logoProfileValue3, matrixStackValue + logoRoundValue
         );
         MatrixStackUtilsAmount.handlePeekPosition(peekPositionLimit - logoProfileValue8, logoProfileValue4 + logoProfileValue5 - logoProfileValue8, logoProfileValue8 * 2.0F, logoProfileValue8 * 2.0F, logoProfileValue8, logoRoundValue, 352321535, peekPositionLimit, logoProfileValue4 - logoRoundValue, logoProfileValue3, logoProfileValue4 + logoProfileValue5);
      }
   }

   private void applyStaggerForSlot(int logoProfileValue) {
      this.entranceAlpha = 1.0F;
      this.entranceScale = 1.0F;
      this.hoverEnabled = true;
      this.hitsEnabled = true;
      if (introStartNanos != 0L) {
         float logoProfileLimit = this.introElapsedMs - logoProfileValue * 20.0F;
         if (!(logoProfileLimit >= 200.0F)) {
            float matrixStackValue = Math.max(0.0F, logoProfileLimit / 200.0F);
            this.entranceAlpha = logoProfileLimit <= 0.0F ? 0.0F : MatrixStackUtilsAmount.getAmountForFloat(logoProfileLimit / 130.0F);
            this.entranceScale = 0.94F + 0.060000002F * MatrixStackUtilsAmount.getAmount(matrixStackValue, 1.6F);
            this.hoverEnabled = matrixStackValue > 0.6F;
            this.hitsEnabled = logoProfileLimit >= 65.0F;
         }
      }
   }

   private void updateBadges(double logoProfileValue) {
      for (int i = 0; i < this.rowCount; i++) {
         Module[] ilIllIiIlls = this.categoryModules[i];
         if (ilIllIiIlls != null) {
            int logoProfileCounter = 0;

            for (Module ilIllIiIllsItem : ilIllIiIlls) {
               if (ilIllIiIllsItem.isEnabled()) {
                  logoProfileCounter++;
               }
            }

            if (logoProfileCounter != this.lastEnabledCounts[i]) {
               this.lastEnabledCounts[i] = logoProfileCounter;
               if (logoProfileCounter > 0) {
                  this.badgeTexts[i] = Integer.toString(logoProfileCounter);
                  if (this.badgesInitialized) {
                     this.rowAnims.handleTime(this.badgeAnimIds[i]);
                  }
               }
            }

            boolean logoProfileFlag = logoProfileCounter > 0;
            this.badgeAlphas[i] = this.badgesInitialized ? animateToward(this.badgeAlphas[i], logoProfileFlag ? 1.0F : 0.0F, logoProfileFlag ? 16.0 : 10.0, logoProfileValue, 0.002F) : (logoProfileFlag ? 1.0F : 0.0F);
            if (this.badgeTexts[i] != null) {
               float thumbAmount = Math.max(14.0F, (float)Math.ceil(MatrixStackUtilsAmount.getAmount(this.badgeTexts[i], 0.78F)) + 7.0F);
               this.badgeWidths[i] = this.badgeWidths[i] > 0.0F && this.badgeAlphas[i] > 0.5F ? animateToward(this.badgeWidths[i], thumbAmount, 18.0, logoProfileValue, 0.05F) : thumbAmount;
            }
         }
      }

      this.badgesInitialized = true;
   }

   private float getTextMaxWidth() {
      return this.expandedWidth - 16 - 13.5F - 1.5F - 37.0F - 1.5F;
   }

   private static ButtonBase findLicenseButton() {
      Module iliIiiliiiii = ModuleRegistry.getModule(License.class);
      if (iliIiiliiiii == null) {
         return null;
      } else {
         for (ConfigSetting autoEspBase : iliIiiliiiii.getSettings()) {
            if (autoEspBase instanceof ButtonBase next) {
               return next;
            }
         }

         return null;
      }
   }

   public void setInteractive(boolean flag) {
      this.interactive = flag;
   }

   private float getLogoIconX() {
      return this.panelX + 16 + -7.0F * this.profile;
   }

   public boolean canConsumeClick(double logoProfileValue, double heightAmount, int button) {
      return this.interactive && button == 0 && this.hitRegions.canAnimsPress(logoProfileValue, heightAmount);
   }

   private void renderCategoryRow(DrawContext context, VertexTextureFont vertexTextureFont, MatrixStack matrices, int value, int logoProfileValue, int logoProfileLimit, int heightValue, double matrixIdentifierFactor) {
      IconsPngType expanditem = this.categories.get(value);
      int logoValueOffset = this.panelX + 8;
      int maxItemWidth = this.currentWidth - 16;
      int matrixScissorLimit = this.rowYs[value];
      if (this.startsSection[value]) {
         this.applyStaggerForSlot(this.staggerSlots[value] - 1);
         if (this.entranceAlpha > 0.002F && this.introFade > 0.002F) {
            MatrixStackUtilsAmount.handlePushScale(
               getSectionLabel(expanditem),
               MatrixStackUtilsAmount.getAmount(this.panelX + 16 - 1.2F),
               this.sectionLabelYs[value] - 9.2F,
               0.8F,
               MatrixStackUtilsAmount.getValue(-11117467, this.entranceAlpha * this.introFade)
            );
         }

         if (value > 0) {
            this.renderDivider(context, this.sectionSeparatorYs[value], this.staggerSlots[value] - 1, this.revealProgress);
         }
      }

      this.applyStaggerForSlot(this.staggerSlots[value]);
      boolean logoProfileFlag = value == logoProfileValue;
      boolean logoProfileFlag2 = this.hoverEnabled && logoProfileLimit >= logoValueOffset && logoProfileLimit < logoValueOffset + maxItemWidth && heightValue >= matrixScissorLimit - this.halfRowGap && heightValue < matrixScissorLimit + this.rowHeight + this.halfRowGap;
      if (logoProfileFlag2) {
         this.hoveredIndex = value;
      }

      this.itemHoverAmounts[value] = animateToward(this.itemHoverAmounts[value], logoProfileFlag2 && !logoProfileFlag ? 1.0F : 0.0F, logoProfileFlag2 ? 16.0 : 11.0, matrixIdentifierFactor, 0.002F);
      this.activeRowAmounts[value] = animateToward(this.activeRowAmounts[value], logoProfileFlag ? 1.0F : 0.0F, 14.0, matrixIdentifierFactor, 0.002F);
      if (!(this.entranceAlpha <= 0.002F)) {
         float logoProfileValue5 = this.itemHoverAmounts[value];
         float cardMatrixFactor = this.activeRowAmounts[value];
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
            context, logoValueOffset + maxItemWidth / 2.0F, matrixScissorLimit + this.rowHeight / 2.0F, this.entranceScale * this.rowAnims.getTime(this.rowAnimIds[value], 0.035F)
         );
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.entranceAlpha);

         try {
            RenderMatrixUtilsRelease.renderForDrawContextFloatDouble(context, logoValueOffset, matrixScissorLimit, maxItemWidth, this.rowHeight, this.indicatorRadius, logoProfileValue5);
            float logoProfile = this.getRowIconX();
            float roundOffset = matrixScissorLimit + (this.rowHeight - 16) / 2.0F;
            int logoProfileValue8 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-10722709, -4604216, logoProfileValue5 * 0.8), MatrixEspUtils.releaseRightValue, cardMatrixFactor);
            MatrixPeekUtilsIdentifier.handle(logoProfile, roundOffset, 16.0F, 16.0F, expanditem.getIdentifier(), new Color(logoProfileValue8, true), matrices);
            if (this.introFade > 0.002F) {
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(this.introFade);

               try {
                  float textsAmounts = this.renderBadgePill(context, value, this.panelX + this.expandedWidth - 8 - 8.0F, matrixScissorLimit, logoProfileValue5, cardMatrixFactor);
                  float matrixStackValue = this.panelX + 45 + (logoProfile - this.panelX - 16.0F);
                  int logoProfileValue10 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-8090988, -920587, logoProfileValue5 * 0.6), MatrixEspUtils.releaseRightValue, cardMatrixFactor);
                  MatrixStackUtilsAmount.handle(
                     vertexTextureFont.formatText(expanditem.getText(), textsAmounts - this.panelX - 45.0F - 1.5F),
                     matrixStackValue,
                     MatrixStackUtilsAmount.getAmount(matrixScissorLimit + this.rowHeight / 2.0F - 8.28F),
                     logoProfileValue10
                  );
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }

            if (this.revealProgress > 0.002F) {
               this.renderCollapsedBadgeDot(context, value, logoProfile + 16.0F - 1.75F, roundOffset + 1.75F, logoProfileValue5, cardMatrixFactor);
            }
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }

         if (this.hitsEnabled) {
            this.hitRegions.handleFlag(logoValueOffset, matrixScissorLimit - this.halfRowGap, maxItemWidth, this.rowHeight + this.halfRowGap * 2, this.actions[value], this.rowAnims, this.rowAnimIds[value]);
         }
      }
   }

   private String getTooltipText(int index) {
      if (index < this.rowCount) {
         return this.categories.get(index).getText();
      } else if (index == this.rowCount) {
         return this.usernameRaw == null ? "" : this.usernameRaw;
      } else {
         return "Expand";
      }
   }

   public SidebarPanel(int logoValue, int value, int value2, int value3, List<IconsPngType> expandItems, Consumer<IconsPngType> consumer) {
      this.hitRegions = new Hits();
      this.rowAnims = new Anims();
      this.highlightSpring = new Spring(0.0);
      this.collapseSpring = new Spring(0.0);
      this.iconScale = 1.0F;
      this.introFade = 1.0F;
      this.hoveredIndex = -1;
      this.lastHoveredIndex = -1;
      this.pendingTooltipIndex = -1;
      this.lastTooltipIndex = -1;
      this.tooltipIndex = -1;
      this.interactive = true;
      this.usernameFitted = "";
      this.accountInfoFitted = "";
      this.entranceAlpha = 1.0F;
      this.entranceScale = 1.0F;
      this.hoverEnabled = true;
      this.hitsEnabled = true;
      this.panelX = logoValue;
      this.panelY = value;
      this.expandedWidth = value2;
      this.currentWidth = value2;
      this.panelHeight = value3;
      this.categories = expandItems;
      this.rowCount = expandItems.size();
      this.rowYs = new int[this.rowCount];
      this.sectionLabelYs = new int[this.rowCount];
      this.staggerSlots = new int[this.rowCount];
      this.lastEnabledCounts = new int[this.rowCount];
      this.sectionSeparatorYs = new float[this.rowCount];
      this.tooltipWidths = new float[this.rowCount + 2];
      this.startsSection = new boolean[this.rowCount];
      this.rowAnimIds = new String[this.rowCount];
      this.badgeAnimIds = new String[this.rowCount];
      this.badgeTexts = new String[this.rowCount];
      this.actions = new Runnable[this.rowCount];
      this.categoryModules = new Module[this.rowCount][];
      this.itemHoverAmounts = new float[this.rowCount];
      this.activeRowAmounts = new float[this.rowCount];
      this.badgeAlphas = new float[this.rowCount];
      this.badgeWidths = new float[this.rowCount];
      PngIcons instance = PngIcons.getInstance();
      int logoProfileCounter = 0;
      int value4 = 1;
      String logoProfileText = null;

      for (int i = 0; i < this.rowCount; i++) {
         IconsPngType iconsPngType = (IconsPngType)expandItems.get(i);
         String sectionLabel = getSectionLabel(iconsPngType);
         if (!sectionLabel.equals(logoProfileText)) {
            this.startsSection[i] = true;
            logoProfileCounter++;
            value4++;
            logoProfileText = sectionLabel;
         }

         this.staggerSlots[i] = value4++;
         this.rowAnimIds[i] = "row:" + iconsPngType.name();
         this.badgeAnimIds[i] = "badge:" + iconsPngType.name();
         this.activeRowAmounts[i] = iconsPngType == instance.getIconsPngType() ? 1.0F : 0.0F;
         this.categoryModules[i] = isGeneralCategory(iconsPngType) ? null : instance.getItems(iconsPngType).toArray(new Module[0]);
         this.actions[i] = () -> {
            if (iconsPngType != PngIcons.getInstance().getIconsPngType()) {
               ClientSounds.updateCategoryClick();
            }

            consumer.accept(iconsPngType);
         };
      }

      this.finalStaggerSlot = value4;
      this.headerDividerYExpanded = value + 15 + 22 + 11;
      this.profileCardY = value + value3 - 8 - 34;
      this.profileDividerYExpanded = this.profileCardY - 11;
      int deltaValue = this.profileDividerYExpanded - this.headerDividerYExpanded;
      int value5 = 16;
      int logoProfileValue = 1;
      int logoProfileValue2 = 6;

      label95:
      for (int j = 20; j >= 16; j -= 2) {
         for (int k = 2; k >= 1; k--) {
            for (int m = 11; m >= 6; m--) {
               int logoProfileValue3 = logoProfileCounter * (m + 5 + 5) + this.rowCount * j + Math.max(0, this.rowCount - logoProfileCounter) * k;
               if (deltaValue - logoProfileValue3 >= 6) {
                  value5 = j;
                  logoProfileValue = k;
                  logoProfileValue2 = m;
                  break label95;
               }
            }
         }
      }

      this.rowHeight = value5;
      this.rowPitch = value5 + logoProfileValue;
      this.halfRowGap = logoProfileValue / 2;
      this.indicatorRadius = Math.min(7, Math.max(4, this.rowHeight / 2 - 2));
      int headerDividerYExpanded22 = this.headerDividerYExpanded;

      for (int p = 0; p < this.rowCount; p++) {
         int logoProfileValue4;
         if (this.startsSection[p]) {
            this.sectionLabelYs[p] = headerDividerYExpanded22 + logoProfileValue2 + 5;
            logoProfileValue4 = this.sectionLabelYs[p] + 5;
         } else {
            logoProfileValue4 = headerDividerYExpanded22 + logoProfileValue;
         }

         this.rowYs[p] = logoProfileValue4;
         headerDividerYExpanded22 = logoProfileValue4 + this.rowHeight;
      }

      for (int q = 1; q < this.rowCount; q++) {
         if (this.startsSection[q]) {
            this.sectionSeparatorYs[q] = (this.rowYs[q - 1] + this.rowHeight + this.rowYs[q]) / 2.0F;
         }
      }

      this.headerDividerYCollapsed = this.rowCount > 0 ? (value + 15 + 22 + this.rowYs[0]) / 2.0F : this.headerDividerYExpanded;
      this.profileDividerYCollapsed = this.rowCount > 0 ? (this.rowYs[this.rowCount - 1] + this.rowHeight + this.profileCardY + 5.0F) / 2.0F : this.profileDividerYExpanded;
      ButtonBase next = findLicenseButton();
      this.profileAction = next == null ? null : next::updateAction;
      this.collapseSpring.handleAmount(instance.isBooleanValue() ? 1.0 : 0.0);
      this.updateCollapseDerivedValues();
      int identityHashCode = System.identityHashCode(Module.mc.currentScreen);
      if (identityHashCode != lastScreenHash) {
         lastScreenHash = identityHashCode;
         justCreated = true;
      }
   }

   public int getCurrentWidth() {
      return this.currentWidth;
   }

   private float getHighlightX() {
      float logoProfileValue = 20.0F;
      float logoProfileValue2 = this.panelX + (40.0F - logoProfileValue) / 2.0F;
      float logoValueOffset = this.panelX + 8;
      return logoValueOffset + (logoProfileValue2 - logoValueOffset) * this.profile;
   }

   private float getHighlightCornerSize() {
      float logoProfileValue = Math.max(1.0F, (float)(this.rowPitch - this.rowHeight));
      return Math.max(1.0F, Math.min(5.0F, Math.min(this.rowHeight * 0.25F, logoProfileValue * 0.5F + 0.5F)));
   }

   private static String fitAccountText(VertexTextureFont vertexTextureFont, String logoProfileText, float logoProfileValue) {
      int indexOf = logoProfileText.indexOf(", ");
      if (indexOf >= 0 && vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(logoProfileText) > logoProfileValue) {
         logoProfileText = logoProfileText.substring(indexOf + 2);
      }

      int indexOf2 = logoProfileText.indexOf(" (");
      if (indexOf2 > 0 && logoProfileText.endsWith(")") && vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(logoProfileText) > logoProfileValue) {
         logoProfileText = logoProfileText.substring(indexOf2 + 2, logoProfileText.length() - 1);
      }

      return vertexTextureFont.formatText(logoProfileText, logoProfileValue);
   }

   private void toggleCollapsed() {
      PngIcons instance = PngIcons.getInstance();
      instance.handle(!instance.isBooleanValue());
      ClientSounds.updateCategoryClick();
      this.lastTooltipIndex = -1;
      this.tooltipIndex = -1;
      this.tooltipAlpha = 0.0F;
   }

   private void updateHighlightSpring(int index, double logoProfileValue) {
      if (index >= 0) {
         double springValue = this.rowYs[index];
         if (!this.highlightInitialized) {
            this.highlightSpring.handleAmount(springValue);
            this.highlightInitialized = true;
         } else if (this.highlightSpring.springPosition != springValue || this.highlightSpring.anywayEnableCount != 0.0) {
            int logoProfileDivisor = Math.max(1, (int)Math.ceil(logoProfileValue / 0.004166666666666667));
            double logoProfileValue3 = logoProfileValue / logoProfileDivisor;

            for (int i = 0; i < logoProfileDivisor; i++) {
               this.highlightSpring.handleAmount(springValue, 784.0, 42.0, logoProfileValue3);
            }

            if (Math.abs(this.highlightSpring.springPosition - springValue) < 0.04 && Math.abs(this.highlightSpring.anywayEnableCount) < 1.5) {
               this.highlightSpring.handleAmount(springValue);
            }
         }

         float max4 = MatrixStackUtilsAmount.getMax((float)Math.abs(this.highlightSpring.springPosition - springValue) / this.rowPitch);
         this.iconScale = animateToward(this.iconScale, 1.0F - 0.6F * max4, 22.0, logoProfileValue, 0.004F);
         this.highlightY = MatrixStackUtilsAmount.getAmount((float)this.highlightSpring.springPosition);
      }
   }

   private void updateCollapseDerivedValues() {
      this.profile = (float)Math.max(0.0, Math.min(1.0, this.collapseSpring.springPosition));
      this.currentWidth = Math.round(this.expandedWidth + (40 - this.expandedWidth) * this.profile);
      this.introFade = 1.0F - smoothStep(this.profile / 0.42F);
      this.revealProgress = smoothStep((this.profile - 0.55F) / 0.45F);
   }

   private float renderBadgePill(DrawContext context, int index, float textsAmounts, int logoProfileValue, float cardMatrixFactor, float logoProfileValue3) {
      float panelOverlayLimit = this.badgeAlphas[index];
      String logoProfileText = this.badgeTexts[index];
      if (!(panelOverlayLimit <= 0.01F) && logoProfileText != null) {
         float materialFactor = this.badgeWidths[index];
         float matrixPeekValue = textsAmounts - materialFactor;
         float configsLikeValue = logoProfileValue + (this.rowHeight - 11.0F) / 2.0F;
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, matrixPeekValue + materialFactor / 2.0F, configsLikeValue + 5.5F, this.rowAnims.getTime(this.badgeAnimIds[index], 0.0F));
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            int matrixStackValue = CardMatrixUtils.getValue(MatrixStackUtilsAmount.getValue(-1, 0.06F + 0.03F * cardMatrixFactor), CardMatrixUtils.getReleaseRight(0.2), logoProfileValue3);
            int logoProfileValue9 = CardMatrixUtils.getValue(
               CardMatrixUtils.getValue(-7630437, -5854026, cardMatrixFactor), CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.25), logoProfileValue3
            );
            MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, configsLikeValue, materialFactor, 11.0F, 5.5F, matrixStackValue);
            float logoProfileValue10 = MatrixStackUtilsAmount.getAmount(logoProfileText, 0.78F);
            MatrixStackUtilsAmount.handlePushScale(
               logoProfileText,
               MatrixStackUtilsAmount.getAmount(matrixPeekValue + (materialFactor - logoProfileValue10) / 2.0F - 1.0919999F),
               MatrixStackUtilsAmount.getAmount(configsLikeValue + 5.5F - 6.4584F),
               0.78F,
               logoProfileValue9
            );
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }

         return matrixPeekValue - 2.5F;
      } else {
         return textsAmounts;
      }
   }

   private static String getSectionLabel(IconsPngType iconsPngType) {
      return isGeneralCategory(iconsPngType) ? "GENERAL" : "MODULES";
   }

   private void renderLogoHeader(DrawContext context, MatrixStack matrices, int logoValue, int scaledY, double matrixIdentifierFactor) {
      this.applyStaggerForSlot(0);
      float logoProfile = this.getLogoIconX();
      float roundOffset = this.panelY + 15;
      boolean logoFlag = this.hoverEnabled && MatrixStackUtilsAmount.canFlag(logoValue, scaledY, logoProfile - 3.0F, roundOffset - 3.0F, 28.0F, 28.0F);
      if (logoFlag) {
         this.hoveredIndex = this.rowCount + 1;
      }

      this.logoHoverAmount = animateToward(this.logoHoverAmount, logoFlag ? 1.0F : 0.0F, logoFlag ? 16.0 : 11.0, matrixIdentifierFactor, 0.002F);
      if (!(this.entranceAlpha <= 0.002F)) {
         float y = roundOffset + 11.0F;
         float corzValue = this.panelX + 45 + (logoProfile - this.panelX - 16.0F);
         float logoValue7 = 5.2808F;
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, this.panelX + this.currentWidth / 2.0F, y, this.entranceScale);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.entranceAlpha);

         try {
            boolean canTranslatePush = MatrixStackUtilsAmount.canTranslatePush(context, logoProfile + 11.0F, y, this.rowAnims.getTime("logo", 0.07F));

            try {
               if (!CardMatrixUtils.isModernFlag()) {
                  MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
                     context, logoProfile + 11.0F - 1.0F, y - 1.0F, 2.0F, 2.0F, 1.0F, CardMatrixUtils.getReleaseRight(0.12 + 0.1 * this.logoHoverAmount), 17.0F, 1.0F
                  );
               }

               MatrixPeekUtilsIdentifier.handle(
                  logoProfile, roundOffset, 22.0F, 22.0F, LOGO_TEXTURE, new Color(CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.16 * this.logoHoverAmount)), matrices
               );
            } finally {
               MatrixStackUtilsAmount.renderPopMatrices(context, canTranslatePush);
            }

            if (this.introFade > 0.002F) {
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(this.introFade);

               try {
                  this.renderClientTitle(corzValue, y, logoValue7);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }

         if (this.hitsEnabled) {
            this.hitRegions.handleFlag(logoProfile - 3.0F, roundOffset - 3.0F, 28.0F, 28.0F, this.toggleCollapseAction, this.rowAnims, "logo");
         }
      }
   }

   private void renderActiveRowEdgeStrip(int index) {
      if (index >= 0) {
         this.applyStaggerForSlot(this.staggerSlots[index]);
         float vertexColorFactor = this.entranceAlpha * CorzRoundedUtils.panelOverlayAmount;
         if (!(vertexColorFactor <= 0.002F)) {
            int releaseRight = getAccentColor();
            int vertexColorValue = Math.round((releaseRight >>> 24 & 0xFF) * vertexColorFactor) << 24;
            int vertexColorValue2 = vertexColorValue | releaseRight & 16777215;
            float vertexColorValue3 = 0.6F;
            if (!(vertexColorValue3 <= 0.1F)) {
               float vertexColorValue4 = this.panelX + this.currentWidth;
               float vertexColorValue5 = vertexColorValue4 + vertexColorValue3;
               float highlightY6 = this.highlightY;
               float vertexColorValue6 = this.highlightY + this.rowHeight;
               Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
               VertexConsumer quads = VertexGuiUtils.getQuads();
               quads.vertex(positionMatrix, vertexColorValue4, highlightY6, 0.0F).color(vertexColorValue2);
               quads.vertex(positionMatrix, vertexColorValue4, vertexColorValue6, 0.0F).color(vertexColorValue2);
               quads.vertex(positionMatrix, vertexColorValue5, vertexColorValue6, 0.0F).color(vertexColorValue2);
               quads.vertex(positionMatrix, vertexColorValue5, highlightY6, 0.0F).color(vertexColorValue2);
               VertexGuiUtils.updateBufferClose();
            }
         }
      }
   }

   private static float animateToward(float logoProfileValue, float thumbAmount, double ctlValue, double matrixIdentifierFactor, float logoProfileLimit) {
      float logoProfileValue5 = (float)MatrixIdentifierUtils.getAmount(logoProfileValue, thumbAmount, ctlValue, matrixIdentifierFactor);
      return Math.abs(logoProfileValue5 - thumbAmount) < logoProfileLimit ? thumbAmount : logoProfileValue5;
   }
}
