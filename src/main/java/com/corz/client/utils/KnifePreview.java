package com.corz.client.utils;

import com.corz.client.gui.ButtonMin;
import com.corz.client.gui.GuiClickScreen;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.MatrixStackUtilsRelease;
import com.corz.client.gui.PreviewText;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.StageKnife;
import com.corz.client.render.TextureImageUtils;
import com.corz.client.utils.RotateQuaternionfUtils.Orbit;
import com.corz.client.utils.KnifeCueClock.Sink;
import com.corz.client.utils.KnifeSoundCues.Cue;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.joml.Matrix4f;
import com.corz.client.categories.visuals.SwordInspect;
import com.corz.client.utils.KnifeCueClock;
import com.corz.client.utils.KnifeSoundCues;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.ButterflyRotation;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeSounds;
import com.corz.client.utils.C2Preview;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.KnifeInspectClip;
import com.corz.client.utils.C2Cues;

public class KnifePreview extends GuiClickScreen {
   private static final String[] stilettoRandomTexts = new String[]{"Butterfly", "M9 Bayonet", "Stiletto"};
   private static final String LOADING_SKIN = "  \u00b7  loading skin";
   private String loadingSkinText;
   private boolean stageActive;
   private float thumbsFadeAlpha;
   private float cubeSlideOffset;
   private final Orbit thumbsOrbit;
   private float peekPosition;
   private final IntConsumer finish;
   private float cubeAmount;
   private float thumbsCount;
   private float peekPositionCube;
   private final StageKnife[] in;
   private final ButterflyRotation butterflyRotation = new ButterflyRotation();
   private String append;
   private static final String[] tossTwirlTexts = new String[]{"Spin", "Flip", "Barrel Roll", "Twirl", "Toss"};
   private int loadingSkinValue;
   private KnifeFinish.Finish vanillaInspect;
   private int thumbsValue;
   private final Runnable sound;
   private boolean knifePreviewActive;
   private final SwordInspect swordInspect;
   private int currentInspectIndex;
   private static boolean thumbsInspectsActive;
   private float peekPositionCubeSchematic;
   private float thumbsAmount;
   private boolean loadingSkinFlag;
   private boolean loadingSkinActive;
   private static final float[] tossTwirlAmounts = new float[0];
   private boolean thumbsStageVisible;
   private static boolean soundSlowFlag = true;
   private final Sink thumbsSink;
   private static float heldSeekSeconds = -1.0F;
   private StageKnife stageKnife;
   private float stageCount;
   private final Runnable replay;
   private static boolean slowReplayFlag;
   private boolean thumbsStageActive;
   private final KnifeCueClock knifeCueClock;
   private String schematicsText;
   private final KnifePreview.Track[] loadingModelRandomButterflys;
   private String knifePreviewText;
   private int schematicsValue;
   private float knifePreviewCount;
   private final List<String> inspectsButterflyItems;
   private StageKnife thumbs;
   private String thumbsText;
   private final Runnable slow;
   private int thumbsValueThumbs;
   private float thumbsCooldownSeconds;
   private float cubeSchematicAmount;
   private int stageValue;
   private float loadingSkinCount;

   @Override
   public void handleThumbsAmount(float thumbsFactor) {
      this.thumbsCount += thumbsFactor;
      this.stageCount += thumbsFactor;
      int indexOf = this.inspectsButterflyItems.indexOf(this.swordInspect.formatEnumEnumHelper());
      if (indexOf >= 0 && indexOf != this.previewRail.getFadeValue()) {
         this.previewRail.handleCrossfadeFlag(indexOf, true);
      }

      int thumbsValue = this.previewRail.getFadeValue();
      if (thumbsValue != this.thumbsValueThumbs) {
         this.thumbsValue = this.thumbsValueThumbs;
         this.thumbsCooldownSeconds = 0.32F;
         this.thumbsValueThumbs = thumbsValue;
         if (this.thumbsValueThumbs >= 0) {
            this.loadingModelRandomButterflys[this.thumbsValueThumbs].updateRandomAmount();
         }
      }

      float randomPreviewValue = thumbsFactor * (slowReplayFlag ? 0.5F : 1.0F);
      if (this.thumbsValueThumbs >= 0) {
         KnifePreview.Track track = this.loadingModelRandomButterflys[this.thumbsValueThumbs];
         track.handleRandomPreview(heldSeekSeconds >= 0.0F ? 0.0F : randomPreviewValue);
         if (heldSeekSeconds >= 0.0F) {
            track.playbackPosition = 0.35F + Math.min(heldSeekSeconds, track.butterflyAmount);
         }
      }

      if (this.thumbsValue >= 0 && this.thumbsCooldownSeconds > 0.0F) {
         this.loadingModelRandomButterflys[this.thumbsValue].handleRandomPreview(randomPreviewValue);
         this.thumbsCooldownSeconds -= thumbsFactor;
      }

      this.thumbsOrbit.handleAmountMax(thumbsFactor);
      this.thumbsAmount = AmountPreviewUtils.getAmount(
         this.thumbsAmount, !this.thumbsOrbit.isFlag() && !(Math.abs(this.thumbsOrbit.getMax()) > 1.0F) ? 1.0F : 0.0F, this.thumbsOrbit.isFlag() ? 10.0F : 0.6F, thumbsFactor, 0.001F
      );
      this.handleLoadingSkin(thumbsFactor);

      try {
         this.butterflyRotation.handleStackUpdate(this.swordInspect);
      } catch (Throwable e) {
      }

      if (this.thumbsValueThumbs >= 0) {
         KnifePreview.Track track2 = this.loadingModelRandomButterflys[this.thumbsValueThumbs];
         boolean thumbsFlag = !soundSlowFlag || "Off".equals(this.swordInspect.getKnifeSounds()) || !this.previewRail.isFlag();
         this.knifeCueClock.handleAmountAmounts(track2.soundStamp, track2.randomButterflyTable, track2.butterflyAmount, track2.playbackPosition - 0.35F, thumbsFlag, this.thumbsSink);
      }

      this.handleThumbsStage(thumbsFactor);
      this.thumbsFadeAlpha = AmountPreviewUtils.getAmount(this.thumbsFadeAlpha, this.thumbs.getMinMinMaxTextureSize() > 0 ? 1.0F : 0.0F, 7.0F, thumbsFactor, 0.002F);
      String thumbsText = this.thumbsValueThumbs >= 0 ? this.loadingModelRandomButterflys[this.thumbsValueThumbs].butterflyRandomText : null;
      boolean thumbsFlag2 = thumbsText != null && this.butterflyRotation.canButterflyEmpty(thumbsText);
      if (!Objects.equals(thumbsText, this.thumbsText) || !thumbsFlag2) {
         this.thumbsText = thumbsFlag2 ? thumbsText : null;
         this.cubeAmount = 0.0F;
      }

      this.cubeAmount = AmountPreviewUtils.getAmount(this.cubeAmount, thumbsFlag2 ? 1.0F : 0.0F, 7.0F, thumbsFactor, 0.002F);
   }

   @Override
   public float getSchematicMaterialsAmount() {
      return 500.0F;
   }

   public KnifePreview(Screen screen, SwordInspect swordInspect) {
      super(screen, "Knife Preview");
      this.in = new StageKnife[]{new StageKnife("out"), new StageKnife("in")};
      this.thumbs = new StageKnife("thumbs");
      this.stageKnife = new StageKnife("thumbs next");
      this.thumbsOrbit = new Orbit(0.0F, 8.0F, 1.0F, 0.8F, 1.7F, 45.0F);
      this.knifeCueClock = new KnifeCueClock();
      this.thumbsValueThumbs = -1;
      this.thumbsValue = -1;
      this.thumbsAmount = 1.0F;
      this.stageActive = true;
      this.loadingSkinFlag = true;
      this.loadingSkinActive = true;
      this.currentInspectIndex = -1;
      this.loadingSkinValue = -1;
      this.schematicsValue = -1;
      this.swordInspect = swordInspect;
      this.inspectsButterflyItems = SwordInspect.getStiletto();
      this.loadingModelRandomButterflys = new KnifePreview.Track[this.inspectsButterflyItems.size()];

      for (int i = 0; i < this.loadingModelRandomButterflys.length; i++) {
         this.loadingModelRandomButterflys[i] = new KnifePreview.Track(this.inspectsButterflyItems.get(i));
      }

      this.vanillaInspect = this.getVanillaInspect();
      this.thumbsSink = new Sink() {
         @Override
         public void handle(long knifePreviewValue) {
            KnifeSounds.handleSoundInstance(knifePreviewValue);
         }

         @Override
         public void handleInspectSword(int inspectSwordValue) {
            if (KnifePreview.this.thumbsValueThumbs >= 0) {
               KnifePreview.this.loadingModelRandomButterflys[KnifePreview.this.thumbsValueThumbs].handleButterflyInspect(inspectSwordValue);
            }
         }
      };
      this.finish = this::handleInspectSword;
      this.sound = () -> soundSlowFlag = !soundSlowFlag;
      this.slow = () -> slowReplayFlag = !slowReplayFlag;
      this.replay = this::updateThumbsButterflys;
      int indexOf = this.inspectsButterflyItems.indexOf(swordInspect.formatEnumEnumHelper());
      this.previewRail.handleCrossfadeFlag(Math.max(0, indexOf), false);
      C2Preview.isButterflyFlag();
   }

   private void updateThumbsStage() {
      for (StageKnife stageKnife : this.in) {
         stageKnife.updateTextureClose();
      }

      this.thumbs.updateTextureClose();
      this.stageKnife.updateTextureClose();
      this.currentInspectIndex = -1;
      this.stageActive = true;
      this.thumbsStageActive = false;
   }

   @Override
   public String formatSchematicsText() {
      int loadingSkinValue = this.previewRail.getFadeValue();
      if (loadingSkinValue >= 0 && loadingSkinValue < this.loadingModelRandomButterflys.length) {
         KnifePreview.Track track = this.loadingModelRandomButterflys[loadingSkinValue];
         KnifeFinish.Finish vanillaInspect = this.getVanillaInspect();
         String append = vanillaInspect.corzclientLoadingText + (vanillaInspect == this.vanillaInspect && this.loadingSkinFlag ? "" : "  \u00b7  loading skin");
         if (this.loadingSkinText == null
            || loadingSkinValue != this.loadingSkinValue
            || !Objects.equals(track.butterflyRandomText, this.schematicsText)
            || track.floorMod != this.schematicsValue
            || !append.equals(this.append)) {
            this.loadingSkinValue = loadingSkinValue;
            this.schematicsText = track.butterflyRandomText;
            this.schematicsValue = track.floorMod;
            this.append = append;
            this.loadingSkinText = this.formatRandomInspect(track, append);
         }

         return this.loadingSkinText;
      } else {
         return null;
      }
   }

   @Override
   public int getShowSchematics() {
      return this.inspectsButterflyItems.size();
   }

   private void handleLoadingSkin(float loadingSkinValue) {
      KnifeFinish.Finish finish = this.getVanillaInspect();
      String loadingSkinText = this.thumbsValueThumbs >= 0 ? this.loadingModelRandomButterflys[this.thumbsValueThumbs].butterflyRandomText : null;
      boolean loadingSkinFlag = true;

      for (String stilettoRandomTextsItem : stilettoRandomTexts) {
         if (!stilettoRandomTextsItem.equals(loadingSkinText)) {
            loadingSkinFlag &= ButterflyRotation.canButterflyInstance(stilettoRandomTextsItem, finish);
         }
      }

      boolean canButterflyInstance = ButterflyRotation.canButterflyInstance(loadingSkinText, finish);
      boolean flag = loadingSkinFlag & canButterflyInstance;
      if (!canButterflyInstance) {
         String text = loadingSkinText + "|" + finish.corzclientLoadingText;
         if (!text.equals(this.knifePreviewText)) {
            this.knifePreviewText = text;
            ButterflyRotation.handleButterflyInstance(loadingSkinText, finish);
         }
      }

      if (finish == this.vanillaInspect) {
         this.loadingSkinCount = 0.0F;
         if (canButterflyInstance && !this.loadingSkinFlag) {
            this.previewRail.updateFadeCrossfade();
         }

         if (flag && !this.loadingSkinActive) {
            this.stageActive = true;
         }

         this.loadingSkinFlag = canButterflyInstance;
         this.loadingSkinActive = flag;
      } else {
         this.loadingSkinCount += loadingSkinValue;
         if (canButterflyInstance || this.loadingSkinCount >= 2.5F) {
            this.vanillaInspect = finish;
            this.loadingSkinCount = 0.0F;
            this.loadingSkinFlag = canButterflyInstance;
            this.loadingSkinActive = flag;
            this.stageActive = true;
            this.previewRail.updateFadeCrossfade();
         }
      }
   }

   @Override
   public void renderCubeAmountForDrawContextButtonMin(DrawContext context, ButtonMin buttonMin) {
      TextureImageUtils.renderBackdropZoom(
         context, 0, buttonMin.cubeKnifeAmount, buttonMin.cubeSchematicAmount, buttonMin.cubeSchematicAmount2Cube, buttonMin.cubeSchematicAmount3Cube, buttonMin.renderAmount
      );
      float cubeAmount = buttonMin.getCubeAmount();
      float cubeValue = buttonMin.getCubeSchematic() - buttonMin.cubeSchematicAmount3Cube * 0.03F;
      float cubeBound = buttonMin.cubeSchematicAmount2Cube * 0.34F;
      float fontOffset = buttonMin.cubeSchematicAmount3Cube * 0.34F;
      MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
         context,
         cubeAmount - cubeBound / 2.0F,
         cubeValue - fontOffset / 2.0F,
         cubeBound,
         fontOffset,
         Math.min(cubeBound, fontOffset) / 2.0F,
         CardMatrixUtils.getReleaseRight(0.07),
         Math.min(buttonMin.cubeSchematicAmount2Cube, buttonMin.cubeSchematicAmount3Cube) * 0.22F,
         1.0F
      );
      this.peekPosition = MatrixStackUtilsRelease.getPeekPositionForFloat(buttonMin.cubeKnifeAmount);
      this.peekPositionCube = MatrixStackUtilsRelease.getPeekPosition(buttonMin.cubeSchematicAmount);
      this.cubeSlideOffset = MatrixStackUtilsRelease.getPeekPositionForFloat(buttonMin.cubeKnifeAmount + buttonMin.cubeSchematicAmount2Cube);
      this.peekPositionCubeSchematic = MatrixStackUtilsRelease.getPeekPosition(buttonMin.cubeSchematicAmount + buttonMin.cubeSchematicAmount3Cube);
      this.cubeSchematicAmount = buttonMin.renderAmount * MatrixStackUtilsRelease.getMFloat();
   }

   private void handleFlagStage(float stageValue) {
      if (!(this.stageCount < 0.3F)) {
         int value = this.getMaxInspectsButterflyItems();
         if (this.currentInspectIndex >= 0 && value != this.stageValue) {
            this.currentInspectIndex = -1;
            this.stageActive = true;
         }

         if (this.currentInspectIndex < 0) {
            if (!this.stageActive) {
               if (this.thumbsStageActive) {
                  return;
               }

               this.knifePreviewCount += stageValue;
               if (this.knifePreviewCount < 0.4F) {
                  return;
               }
            }

            this.knifePreviewCount = 0.0F;
            this.thumbsStageVisible = this.stageActive && this.thumbs.getMinMinMaxTextureSize() > 0;
            this.stageActive = false;
            this.stageValue = value;
            this.knifePreviewActive = true;
            this.stageKnife.handleTextureCreate(value * this.inspectsButterflyItems.size(), value);
            this.currentInspectIndex = 0;
         }

         this.knifePreviewActive = this.knifePreviewActive
            & this.butterflyRotation.canRandomStiletto(this.stageKnife, this.currentInspectIndex * value, value, this.inspectsButterflyItems.get(this.currentInspectIndex), this.vanillaInspect);
         if (++this.currentInspectIndex >= this.inspectsButterflyItems.size()) {
            StageKnife thumbs = this.stageKnife;
            this.stageKnife = this.thumbs;
            this.thumbs = thumbs;
            this.currentInspectIndex = -1;
            this.thumbsStageActive = this.knifePreviewActive;
            if (this.thumbsStageVisible) {
               this.thumbsFadeAlpha = Math.min(this.thumbsFadeAlpha, 0.25F);
            }
         }
      }
   }

   @Override
   public float getAmount() {
      return 340.0F;
   }

   @Override
   public boolean isFlag() {
      return true;
   }

   static C2Knife getButterfly(String butterflyText) {
      return "Butterfly".equals(butterflyText) ? C2Knife.BUTTERFLY : C2Knife.getC2Knife(butterflyText);
   }

   public static String formatInspectSeek(String inspectSeekText) {
      String inspectSeekText2 = inspectSeekText == null ? "" : inspectSeekText.trim();
      if (inspectSeekText2.startsWith("inspect")) {
         SwordInspect swordInspect = (SwordInspect)ModuleRegistry.getModule(SwordInspect.class);
         return swordInspect == null ? "no sword inspect" : swordInspect.formatSlotSwapped(inspectSeekText2.substring(7).trim());
      } else if (inspectSeekText2.startsWith("seek")) {
         try {
            heldSeekSeconds = Math.max(0.0F, Float.parseFloat(inspectSeekText2.substring(4).trim()));
         } catch (NumberFormatException e) {
            return "knife seek needs seconds: " + inspectSeekText2;
         }

         return "knife preview held at " + heldSeekSeconds + " s";
      } else {
         heldSeekSeconds = -1.0F;
         return "knife preview playing";
      }
   }

   private String formatRandomInspect(KnifePreview.Track track, String randomInspectText) {
      boolean bayonetStiletto = KnifeInspectClip.canBayonetStiletto(track.butterflyRandomText);
      StringBuilder stringBuilder = new StringBuilder();
      C2Knife c2Knife = C2Knife.getC2Knife(track.butterflyRandomText);
      if (c2Knife != null) {
         stringBuilder.append(track.butterflyRandomText);
         if (track.randomInspectValue > 1) {
            stringBuilder.append("  \u00b7  Inspect ").append(track.floorMod + 1).append(" of ").append(track.randomInspectValue);
         }

         stringBuilder.append("  \u00b7  ").append(c2Knife.formatPremierOperative()).append("  \u00b7  Knives by dc");
         return stringBuilder.toString();
      } else {
         if ("Random".equals(track.randomKnivesText)) {
            stringBuilder.append("Random  \u00b7  now ").append(track.butterflyRandomText);
         } else {
            stringBuilder.append(track.butterflyRandomText);
         }

         if ("Butterfly".equals(track.butterflyRandomText) && track.randomInspectValue > 0) {
            stringBuilder.append("  \u00b7  Inspect ").append(track.floorMod + 1).append(" of ").append(track.randomInspectValue);
         }

         if (bayonetStiletto) {
            stringBuilder.append("  \u00b7  ").append(randomInspectText);
         } else {
            stringBuilder.append("  \u00b7  Sword trick");
         }

         return stringBuilder.toString();
      }
   }

   @Override
   public void renderCubeAmount(DrawContext context, ButtonMin buttonMin) {
      int index = this.previewRail.getFadeValue();
      if (index >= 0 && index < this.loadingModelRandomButterflys.length) {
         KnifePreview.Track track = this.loadingModelRandomButterflys[index];
         if (track.butterflyAmount > 0.0F && !(this.cubeAmount <= 0.002F)) {
            float cubeValue = Math.min(
               AmountPreviewUtils.getMin(track.playbackPosition / 0.35F), AmountPreviewUtils.getMin((0.35F + track.butterflyAmount + 0.8F - track.playbackPosition) / 0.25F)
            );
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(AmountPreviewUtils.getAmount6(this.cubeAmount) * AmountPreviewUtils.getAmount8(cubeValue));

            try {
               this.renderAmountCube(context, buttonMin, track);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      }
   }

   @Override
   public float getMax() {
      return 134.0F;
   }

   @Override
   public void handleInspectsSchematics(int i) {
      if (i >= 0 && i < this.inspectsButterflyItems.size()) {
         this.swordInspect.handleAutoEnum(this.inspectsButterflyItems.get(i));
      }
   }

   @Override
   public boolean canAmountMatrix(ButtonMin buttonMin, double matrixValue, double matrixValue2, double exponent) {
      this.thumbsOrbit.handleAmount(exponent);
      return true;
   }

   @Override
   protected void updateMatrixStack3() {
      this.knifeCueClock.handle(this.thumbsSink);
      this.updateThumbsStage();
   }

   @Override
   public void handleMatrixStack(ButtonMin buttonMin, double matrixStackValue, double matrixStackValue2, int matrixStackValue3) {
      if (this.thumbsOrbit.isFlag()) {
         this.thumbsOrbit.updateMaxFlag();
      }
   }

   @Override
   public void removed() {
      super.removed();
      this.knifeCueClock.handle(this.thumbsSink);
      this.updateThumbsStage();
   }

   private void renderAmountCube(DrawContext context, ButtonMin buttonMin, KnifePreview.Track track) {
      float cubeValue = 14.0F;
      float matrixPeekValue = buttonMin.cubeKnifeAmount + cubeValue;
      float cubeValue3 = buttonMin.cubeKnifeAmount + buttonMin.cubeSchematicAmount2Cube - cubeValue;
      float f2 = buttonMin.cubeSchematicAmount + buttonMin.cubeSchematicAmount3Cube - 11.0F;
      float deltaVar = cubeValue3 - matrixPeekValue;
      float rootDivisor = MatrixPeekUtilsRender.getAmount();
      float cubeLimit = track.playbackPosition - 0.35F;
      float min2 = AmountPreviewUtils.getMin(cubeLimit / track.butterflyAmount);
      float cubeLimit3 = cubeLimit > track.butterflyAmount ? 1.0F - AmountPreviewUtils.getAmount8((cubeLimit - track.butterflyAmount) / 0.8F) : 1.0F;
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, f2, deltaVar, rootDivisor, MatrixStackUtilsRelease.getValue(-1, 0.1F));
      if (min2 > 0.0F && cubeLimit3 > 0.002F) {
         MatrixPeekUtilsRender.renderMatrixPeek(
            context, matrixPeekValue, f2, deltaVar * min2, rootDivisor, MatrixStackUtilsRelease.getValue(MatrixEspUtils.releaseRightValue, 0.85F * cubeLimit3)
         );
      }

      float[] cubeValues = track.randomButterflyTable;

      for (int i = 0; i < cubeValues.length; i++) {
         float cubeValue6 = matrixPeekValue + deltaVar * AmountPreviewUtils.getMin(cubeValues[i] / track.butterflyAmount);
         float cubeLimit4 = (track.playbackPosition - 0.35F - cubeValues[i]) / (slowReplayFlag ? 0.5F : 1.0F);
         float cubeLimit5 = cubeLimit4 >= 0.0F && cubeLimit4 < 0.35F ? 1.0F - AmountPreviewUtils.getAmount6(cubeLimit4 / 0.35F) : 0.0F;
         float cardMatrixFactor = cubeLimit >= cubeValues[i] ? cubeLimit3 : 0.0F;
         float corzLicenseLimit = 1.4F + 1.3F * cubeLimit5;
         int cardMatrixValue = CardMatrixUtils.getValue(
            MatrixStackUtilsRelease.getValue(-1, 0.28F), MatrixStackUtilsRelease.getValue(MatrixEspUtils.releaseRightValue, 0.9F), cardMatrixFactor
         );
         int matrixStackValue = CardMatrixUtils.getValue(cardMatrixValue, -1, 0.6 * cubeLimit5);
         if (cubeLimit5 > 0.01F) {
            MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
               context,
               cubeValue6 - corzLicenseLimit,
               f2 + rootDivisor / 2.0F - corzLicenseLimit,
               corzLicenseLimit * 2.0F,
               corzLicenseLimit * 2.0F,
               corzLicenseLimit,
               MatrixStackUtilsRelease.getValue(MatrixEspUtils.releaseRightValue, 0.35F * cubeLimit5),
               5.0F * cubeLimit5,
               1.0F
            );
         }

         MatrixPeekUtilsRender.renderMatrixPeek(context, cubeValue6 - corzLicenseLimit, f2 + rootDivisor / 2.0F - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, matrixStackValue);
      }
   }

   private KnifeFinish.Finish getVanillaInspect() {
      KnifeFinish.Finish finish = KnifeFinish.getFinish(this.swordInspect.getKnifeFinish());
      return finish != null ? finish : KnifeFinish.getFinish("Vanilla");
   }

   @Override
   public void renderCubeSchematic(DrawContext context, ButtonMin buttonMin) {
      int index = buttonMin.selectedIndex;
      if (index >= 0 && index < this.loadingModelRandomButterflys.length) {
         KnifePreview.Track track = this.loadingModelRandomButterflys[index];
         if (!this.butterflyRotation.canButterflyEmpty(track.butterflyRandomText)) {
            String cubeSchematicText = this.butterflyRotation.canButterfly(track.butterflyRandomText) ? "This knife could not be loaded." : "The model is still loading.";
            MatrixStackUtilsRelease.render(
               context,
               buttonMin.cubeKnifeAmount,
               buttonMin.cubeSchematicAmount,
               buttonMin.cubeSchematicAmount2Cube,
               buttonMin.cubeSchematicAmount3Cube,
               MatrixStackUtilsRelease.getIdentifier("cube"),
               "Loading knife",
               cubeSchematicText
            );
         } else if (!StageKnife.isValidPrecompile()) {
            throw new IllegalStateException("knife stage shader did not compile");
         } else {
            MinecraftClient instance = MinecraftClient.getInstance();
            float scaleFactor = instance.getWindow().getScaleFactor();
            float peekPosition = MatrixStackUtilsRelease.getPeekPositionForFloat(buttonMin.cubeKnifeAmount);
            float peekPosition2 = MatrixStackUtilsRelease.getPeekPosition(buttonMin.cubeSchematicAmount);
            float peekPositionForFloat = MatrixStackUtilsRelease.getPeekPositionForFloat(buttonMin.cubeKnifeAmount + buttonMin.cubeSchematicAmount2Cube);
            float peekPosition4 = MatrixStackUtilsRelease.getPeekPosition(buttonMin.cubeSchematicAmount + buttonMin.cubeSchematicAmount3Cube);
            int maxRound = Math.max(1, Math.round((peekPositionForFloat - peekPosition) * scaleFactor * 2.0F));
            int botsMatrixDivisor = Math.max(1, Math.round((peekPosition4 - peekPosition2) * scaleFactor * 2.0F));
            StageKnife stageKnife = this.in[buttonMin.guidePackageFlag ? 1 : 0];
            stageKnife.handleTextureCreate(maxRound, botsMatrixDivisor);
            float cubeSchematicValue = this.thumbsOrbit.getSmoothedYaw() + 22.0F * (float)Math.sin(this.thumbsCount * 0.42F) * this.thumbsAmount;
            this.butterflyRotation
               .handleAmountStiletto(
                  stageKnife,
                  maxRound,
                  botsMatrixDivisor,
                  track.butterflyRandomText,
                  track.randomButterflyText,
                  track.knifeInspectClip,
                  track.playbackPosition - 0.35F,
                  track.butterflyAmount,
                  cubeSchematicValue,
                  this.thumbsOrbit.getSmoothedPitch(),
                  this.thumbsOrbit.getSmoothedDistance(),
                  this.vanillaInspect
               );
            Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
            stageKnife.handleBufferSampler(
               positionMatrix,
               buttonMin.cubeKnifeAmount,
               buttonMin.cubeSchematicAmount,
               buttonMin.cubeSchematicAmount2Cube,
               buttonMin.cubeSchematicAmount3Cube,
               stageKnife.getMin(0),
               stageKnife.getMinForInt(0),
               stageKnife.getMin(maxRound),
               stageKnife.getMinForInt(botsMatrixDivisor),
               this.peekPosition,
               this.peekPositionCube,
               this.cubeSlideOffset,
               this.peekPositionCubeSchematic,
               this.cubeSchematicAmount,
               CorzRoundedUtils.getPanelOverlay()
            );
         }
      }
   }

   @Override
   public void handleMatrixParse(ButtonMin buttonMin, double matrixParseValue, double matrixParseValue2, double orbitValue, double rotateQuaternionfValue, int matrixParseValue5) {
      if (matrixParseValue5 == 0) {
         this.thumbsOrbit.handleAmount(orbitValue, rotateQuaternionfValue);
      }
   }

   private int getMaxInspectsButterflyItems() {
      MinecraftClient instance = MinecraftClient.getInstance();
      int maxRound = Math.max(8, Math.round(22.0F * instance.getWindow().getScaleFactor() * 2.0F));
      return Math.max(1, Math.min(maxRound, StageKnife.getMaxDevice() / Math.max(1, this.inspectsButterflyItems.size())));
   }

   private boolean isRandomButterflys() {
      int index = this.previewRail.getFadeValue();
      return index >= 0
         && index < this.loadingModelRandomButterflys.length
         && C2Knife.getC2Knife(this.loadingModelRandomButterflys[index].butterflyRandomText) != null;
   }

   private void handleInspectSword(int inspectSwordValue) {
      if (!this.isRandomButterflys()) {
         this.swordInspect.handleEnum(inspectSwordValue);
      }
   }

   @Override
   public void renderThumbsInspects(DrawContext context, int thumbsInspectsFactor, float thumbsInspectsValue, float peekPositionFactor, float bufferSamplerValue) {
      if (!thumbsInspectsActive && this.thumbs.getMinMinMaxTextureSize() > 0 && !(this.thumbsFadeAlpha <= 0.002F)) {
         int maxInspectsButterflyItems = this.getMaxInspectsButterflyItems();
         if (this.thumbs.getMinMinMaxTextureSize() >= maxInspectsButterflyItems * this.inspectsButterflyItems.size()) {
            Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
            float thumbsInspectsValue4 = MatrixPeekUtilsRender.getAmount();

            try {
               this.thumbs
                  .handleBufferSampler(
                     positionMatrix,
                     thumbsInspectsValue,
                     peekPositionFactor,
                     bufferSamplerValue,
                     bufferSamplerValue,
                     this.thumbs.getMin(thumbsInspectsFactor * maxInspectsButterflyItems),
                     this.thumbs.getMinForInt(0),
                     this.thumbs.getMin((thumbsInspectsFactor + 1) * maxInspectsButterflyItems),
                     this.thumbs.getMinForInt(maxInspectsButterflyItems),
                     MatrixStackUtilsRelease.getPeekPositionForFloat(thumbsInspectsValue + thumbsInspectsValue4),
                     MatrixStackUtilsRelease.getPeekPosition(peekPositionFactor + thumbsInspectsValue4),
                     MatrixStackUtilsRelease.getPeekPositionForFloat(thumbsInspectsValue + bufferSamplerValue - thumbsInspectsValue4),
                     MatrixStackUtilsRelease.getPeekPosition(peekPositionFactor + bufferSamplerValue - thumbsInspectsValue4),
                     (5.0F - thumbsInspectsValue4) * MatrixStackUtilsRelease.getMFloat(),
                     CorzRoundedUtils.getPanelOverlay() * this.thumbsFadeAlpha
                  );
            } catch (Throwable e) {
               thumbsInspectsActive = true;
            }
         }
      }
   }

   @Override
   public String formatSoldBought(int i) {
      String inspectsbutterflyitem = this.inspectsButterflyItems.get(i);
      if ("Butterfly".equals(inspectsbutterflyitem)) {
         return "Knife \u00b7 3 inspects";
      } else {
         C2Knife c2Knife = C2Knife.getC2Knife(inspectsbutterflyitem);
         if (c2Knife != null) {
            return c2Knife.formatPremierOperative();
         } else if (KnifeInspectClip.canBayonetStiletto(inspectsbutterflyitem)) {
            return "Knife";
         } else {
            return "Random".equals(inspectsbutterflyitem) ? "New one each press" : "Sword trick";
         }
      }
   }

   @Override
   public boolean canMaxSubstring(int substringValue, int substringValue2) {
      switch (substringValue) {
         case 32:
            this.updateThumbsButterflys();
            break;
         case 91:
            this.handleInspectSword(-1);
            break;
         case 93:
            this.handleInspectSword(1);
            break;
         default:
            return false;
      }

      return true;
   }

   private void updateThumbsButterflys() {
      if (this.thumbsValueThumbs >= 0) {
         this.loadingModelRandomButterflys[this.thumbsValueThumbs].updateRandomAmount();
         this.previewRail.updateFadeCrossfade();
      }
   }

   private void handleThumbsStage(float thumbsStageValue) {
      if (!thumbsInspectsActive && StageKnife.isValidPrecompile()) {
         try {
            this.handleFlagStage(thumbsStageValue);
         } catch (Throwable e) {
            thumbsInspectsActive = true;
            this.thumbs.updateTextureClose();
            this.stageKnife.updateTextureClose();
            this.currentInspectIndex = -1;
         }
      }
   }

   @Override
   public void handleFolderDone(PreviewText previewText) {
      if (this.isRandomButterflys()) {
         previewText.getClassForString("finish_own", "Own skin");
      } else {
         String folderDoneText = this.vanillaInspect == null ? "Vanilla" : this.swordInspect.getKnifeFinish();
         previewText.getClassForStringIntConsumer("finish", "Finish", folderDoneText, this.finish);
      }

      previewText.getClassForStringFlag("sound", "Sound", soundSlowFlag, this.sound)
         .getClassForStringFlag("slow", "Slow-mo", slowReplayFlag, this.slow)
         .getFlag()
         .getClassPreviewText("replay", "Replay", this.replay);
   }

   @Override
   public String formatTextsInspects(int i) {
      return this.inspectsButterflyItems.get(i);
   }

   @Override
   public boolean canMatrixStack(ButtonMin buttonMin, double matrixStackValue, double matrixStackValue2, int matrixStackValue3) {
      if (matrixStackValue3 == 1) {
         this.thumbsOrbit.updateAmountMax();
         return true;
      } else {
         this.thumbsOrbit.updateMaxAmount();
         return true;
      }
   }

   private final class Track {
      final String randomKnivesText;
      KnifeInspectClip knifeInspectClip;
      float butterflyAmount;
      List<C2Cues.Contact> butterflyItems;
      int randomCount;
      List<Cue> minecraftBlock;
      int floorMod;
      String butterflyRandomText;
      long soundStamp;
      float playbackPosition;
      String randomButterflyText;
      float[] randomButterflyTable = KnifePreview.tossTwirlAmounts;
      int randomInspectValue;

      void updateRandomAmount() {
         this.playbackPosition = 0.0F;
         this.randomCount = 0;
         this.updateRandomButterfly();
      }

      Track(String randomKnivesText) {
         this.butterflyItems = List.of();
         this.minecraftBlock = List.of();
         this.randomKnivesText = randomKnivesText;
         this.butterflyRandomText = randomKnivesText;
      }

      void handleButterflyInspect(int index) {
         String butterflyInspectText;
         float butterflyInspectFactor;
         float butterflyInspectValue;
         if (!this.butterflyItems.isEmpty()) {
            if (index < 0 || index >= this.butterflyItems.size()) {
               return;
            }

            C2Cues.Contact butterflyitem = this.butterflyItems.get(index);
            butterflyInspectText = butterflyitem.sound();
            butterflyInspectFactor = butterflyitem.volume();
            butterflyInspectValue = butterflyitem.pitch();
         } else {
            if (index < 0 || index >= this.minecraftBlock.size()) {
               return;
            }

            Cue cue = this.minecraftBlock.get(index);
            butterflyInspectText = cue.sound();
            butterflyInspectFactor = cue.volume();
            butterflyInspectValue = cue.pitch();
         }

         KnifeSounds.handleMinecraftPlay(
            KnifePreview.this.swordInspect.getKnifeSounds(), butterflyInspectText, butterflyInspectFactor * KnifePreview.this.swordInspect.getAmount() * 0.6F, butterflyInspectValue, this.soundStamp
         );
      }

      void updateRandomButterfly() {
         this.butterflyRandomText = this.randomKnivesText;
         if ("Random".equals(this.randomKnivesText)) {
            String[] texts = KnifePreview.this.swordInspect.isEnum() ? KnifePreview.stilettoRandomTexts : KnifePreview.tossTwirlTexts;
            this.butterflyRandomText = texts[Math.floorMod(this.randomCount, texts.length)];
         }

         this.randomButterflyText = null;
         this.knifeInspectClip = null;
         this.butterflyItems = List.of();
         this.minecraftBlock = List.of();
         this.floorMod = 0;
         this.randomInspectValue = 0;
         C2Knife butterfly = KnifePreview.getButterfly(this.butterflyRandomText);
         if (butterfly != null) {
            var list = C2Preview.getItems(butterfly);
            this.randomInspectValue = list.size();
            if (this.randomInspectValue > 0) {
               this.floorMod = Math.floorMod(
                  "Random".equals(this.randomKnivesText) ? this.randomCount / KnifePreview.stilettoRandomTexts.length : this.randomCount,
                  this.randomInspectValue
               );
               this.randomButterflyText = (String)list.get(this.floorMod);
               this.butterflyAmount = C2Preview.getLength(butterfly, this.randomButterflyText);
               this.butterflyItems = C2Preview.getModel(butterfly, this.randomButterflyText);
            } else {
               this.butterflyAmount = SwordInspect.getSpins("Butterfly");
            }
         } else if (KnifeInspectClip.canBayonetStiletto(this.butterflyRandomText)) {
            this.knifeInspectClip = SwordInspect.getAnticipationSpins(this.butterflyRandomText);
            this.butterflyAmount = SwordInspect.getSpins(this.butterflyRandomText);
            this.minecraftBlock = KnifeSoundCues.getMinecraftBlock(this.butterflyRandomText, this.knifeInspectClip, this.butterflyAmount);
         } else {
            this.butterflyAmount = SwordInspect.getSpins(this.butterflyRandomText);
         }

         int randomButterflySize = this.butterflyItems.isEmpty() ? this.minecraftBlock.size() : this.butterflyItems.size();
         this.randomButterflyTable = randomButterflySize == 0 ? KnifePreview.tossTwirlAmounts : new float[randomButterflySize];

         for (int i = 0; i < randomButterflySize; i++) {
            this.randomButterflyTable[i] = this.butterflyItems.isEmpty() ? this.minecraftBlock.get(i).p() * this.butterflyAmount : this.butterflyItems.get(i).time();
         }

         this.soundStamp = KnifeSounds.getTime();
      }

      void handleRandomPreview(float randomPreviewValue) {
         C2Knife butterfly = KnifePreview.getButterfly(this.butterflyRandomText);
         if (butterfly != null && this.randomButterflyText == null && C2Preview.canFlag(butterfly)) {
            this.playbackPosition = 0.0F;
            this.updateRandomButterfly();
            if (this == KnifePreview.this.loadingModelRandomButterflys[Math.max(0, KnifePreview.this.thumbsValueThumbs)]) {
               KnifePreview.this.previewRail.updateFadeCrossfade();
            }
         }

         this.playbackPosition += randomPreviewValue;
         float randomPreviewValue2 = 0.35F + this.butterflyAmount + 0.8F;
         if (this.playbackPosition >= randomPreviewValue2) {
            this.playbackPosition -= randomPreviewValue2;
            this.randomCount++;
            String butterflyRandomText = this.butterflyRandomText;
            this.updateRandomButterfly();
            if (!Objects.equals(butterflyRandomText, this.butterflyRandomText)
               && KnifePreview.this.thumbsValueThumbs >= 0
               && this == KnifePreview.this.loadingModelRandomButterflys[KnifePreview.this.thumbsValueThumbs]) {
               KnifePreview.this.previewRail.updateFadeCrossfade();
            }
         }
      }
   }
}
