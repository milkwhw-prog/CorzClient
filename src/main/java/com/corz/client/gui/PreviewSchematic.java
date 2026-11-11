package com.corz.client.gui;

import com.corz.client.config.SchematicLoader;
import com.corz.client.render.BakedQuadUtils;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.GpuBufferHelper;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixRotateUtilsAmount2;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.SchemCorzUtils;
import com.corz.client.render.TextureImageUtils;
import com.corz.client.world.ElseBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import com.corz.client.utils.AmountPreviewUtils;
import com.corz.client.utils.RotateQuaternionfUtils;

public class PreviewSchematic extends GuiClickScreen {
   private float parseFloat;
   private int layersAllValue;
   private float panOffsetY;
   private static final String PARTIAL_PREVIEW = "Partial preview";
   private long lastNanoTime;
   private static final String FILE_MISSING = "File missing: ";
   private int layersAllValueChevronLnext;
   private final IntConsumer intConsumer;
   private float loadingSchematicAmount;
   private final SchematicLoader schematicBuilder;
   private final Runnable resetAction;
   private final Matrix4f cubeMatrix4f;
   private boolean parseActive;
   private String append;
   private String gpuMeshText;
   private String meshText;
   private int lastLayersMax;
   private float matrixAmount;
   private final IntConsumer chevronLnextIntConsumer;
   private static final String CORZCLIENT_SCHEMATICS = "corzclient/schematics";
   private float parseCount;
   private String schematicsText;
   private int layerCount;
   private final Runnable folder;
   private int floorMod;
   private long lastNanoTimeLoadingSchematic;
   private static final String LOAD = "Failed to load ";
   private String[] schematicsTexts;
   private String schematicsSelectedText;
   private static final String SEPARATOR_CHARS = " -_+&\u00b7.,";
   private String partialPreviewText;
   private ElseBuilder activeBuilderEntry;
   private String layersText;
   private final Map<String, PreviewSchematic.Slot> gpuMeshMap;
   private long lastNanoTimeThumbs;
   private static final Pattern compile = Pattern.compile("\\s*[\\[({][^\\])}]*[\\])}]?");
   private static boolean thumbsActive;
   private static final String DRAG_ROTATE_SCROLL = "Drag to rotate  \u00b7  Scroll to zoom  \u00b7  Right-drag to pan";
   private int selectedSchematicsValue;
   private List<String> copyOf;
   private static final Pattern previewSchematicPattern = Pattern.compile("(?i)(\\S+)\\s+by\\s+(\\S+)$");
   private int schematicsValue;
   private final RotateQuaternionfUtils.Orbit cubeOrbit = new RotateQuaternionfUtils.Orbit(35.0F, 26.0F, 1.0F, 0.45F, 12.0F, 80.0F).getAmount(9.0F);
   private float layersAllAmount;
   private static final Set<String> REQUEST_DESIGN_SET = Set.of("me", "you", "us", "them", "hand", "day", "night", "default", "itself", "design", "request");
   private final Runnable chevronLnextAction;
   private float dragPanAmount;
   private float uploadParseCount;
   private String previewSchematicText;
   private final Runnable lnextChevronAction;
   private float chevronLnextAmount;
   private int previewSchematicValue;
   private float floatParse;
   private static final String RESET_VIEW = "Reset view";
   private String layersAllText;
   private String[] schematicsSelectedTexts;
   private static final String LOADING_SCHEMATIC = "Loading schematic";
   private static PreviewSchematic thisMeshGpu;
   private float thumbsAmount;
   private int lastLayersMin;

   protected final int getSchematicsTexts() {
      return this.schematicsSelectedTexts.length;
   }

   protected final void update() {
      this.updateBuilderSchematic();
   }

   protected final int getSelectedSchematicsValue() {
      return this.selectedSchematicsValue;
   }

   @Override
   protected void updateMatrixStack3() {
      this.updateMeshGpu();
   }

   @Override
   public float getMax() {
      return 134.0F;
   }

   private void renderChevronLnext(DrawContext context, ButtonMin buttonMin, float chevronLnextValue, float configsLikeValue, float materialFactor, boolean chevronLnextFlag) {
      int layersAllValue = this.layerCount;
      if (layersAllValue > 1) {
         String formatLayersAll = this.formatLayersAll();
         if (layersAllValue != this.layersAllValueChevronLnext) {
            this.layersAllValueChevronLnext = layersAllValue;
            this.chevronLnextAmount = 42.0F + (float)Math.ceil(MatrixStackUtilsRelease.getAmount("Layer " + layersAllValue + " of " + layersAllValue, 0.72F));
         }

         float chevronLnextAmount5 = this.chevronLnextAmount;
         boolean chevronLnextFlag2 = this.floorMod >= 0;
         MatrixStackUtilsRelease.renderReleaseRight(context, chevronLnextValue, configsLikeValue, chevronLnextAmount5, materialFactor, 4.0F, 0.0F, chevronLnextFlag2 ? 1.0F : 0.0F);
         int chevronLnextLimit = chevronLnextFlag2 ? this.floorMod : layersAllValue - 1;
         this.render(context, buttonMin, "ov:lprev", IcoGenUtils.getIdentifier("chevron-left"), chevronLnextValue, configsLikeValue, materialFactor, chevronLnextFlag && chevronLnextLimit > 0, this.chevronLnextAction);
         this.render(context, buttonMin, "ov:lnext", IcoGenUtils.getIdentifier("chevron-right"), chevronLnextValue + chevronLnextAmount5 - 12.0F, configsLikeValue, materialFactor, chevronLnextFlag && chevronLnextFlag2, this.lnextChevronAction);
         float cubeSchematicValue = chevronLnextAmount5 - 24.0F;
         float chevronLnextValue5 = 10.0F + MatrixStackUtilsRelease.getAmount(formatLayersAll, 0.72F);
         float chevronLnextValue6 = MatrixStackUtilsAmount.getAmount(chevronLnextValue + 12.0F + (cubeSchematicValue - chevronLnextValue5) / 2.0F);
         float releaseRightValue = chevronLnextFlag ? buttonMin.getMin("ov:layer", chevronLnextValue + 12.0F, configsLikeValue, cubeSchematicValue, materialFactor) : 0.0F;
         int matrixStackValue = chevronLnextFlag2 ? MatrixStackUtilsRelease.getReleaseRight(releaseRightValue, 1.0F) : CardMatrixUtils.getValue(-7630437, -920587, releaseRightValue);
         MatrixStackUtilsRelease.handle(IcoGenUtils.getIdentifier("layers"), chevronLnextValue6, MatrixStackUtilsAmount.getAmount(configsLikeValue + (materialFactor - 7.0F) / 2.0F), 7.0F, matrixStackValue);
         MatrixStackUtilsRelease.handle(formatLayersAll, chevronLnextValue6 + 7.0F + 3.0F, configsLikeValue, materialFactor, 0.72F, matrixStackValue);
         if (chevronLnextFlag) {
            buttonMin.handle("ov:layer", chevronLnextValue + 12.0F, configsLikeValue, cubeSchematicValue, materialFactor, this.chevronLnextIntConsumer);
         }
      }
   }

   private static float getAmount(String previewSchematicText) {
      return (float)Math.ceil(MatrixStackUtilsRelease.getAmount(previewSchematicText, 0.7F)) + 10.0F;
   }

   protected final void updateSchematicsSelected() {
      if (this.selectedSchematicsValue >= 0) {
         this.previewRail.handleCrossfadeFlag(this.selectedSchematicsValue, false);
      }
   }

   private static String formatText(String previewSchematicText) {
      int lastIndexOf = previewSchematicText.lastIndexOf(46);
      return lastIndexOf > 0 ? previewSchematicText.substring(0, lastIndexOf) : previewSchematicText;
   }

   private static void render(DrawContext context, String previewSchematicText, float previewSchematicValue, float configsLikeValue, float previewSchematicValue3, float materialFactor, int matrixStackValue) {
      MatrixStackUtilsRelease.renderReleaseRight(context, previewSchematicValue, configsLikeValue, previewSchematicValue3, materialFactor, 4.0F, 0.0F, 0.0F);
      MatrixStackUtilsRelease.handle(previewSchematicText, previewSchematicValue + 5.0F, configsLikeValue, materialFactor, 0.7F, matrixStackValue);
   }

   private void updateMinMax() {
      this.cubeOrbit.getAmount(0.0F);
      thumbsActive = true;
   }

   private void handleCapsSchem(PreviewSchematic.Slot slot, BakedQuadUtils.CapsJob capsJob) {
      BakedQuadUtils.Caps colorsModelsCaps = capsJob.getColorsModelsCaps();

      GpuBufferHelper gpuBufferHelper;
      try {
         gpuBufferHelper = GpuBufferHelper.getVertexClose(colorsModelsCaps.unfinishedSink, colorsModelsCaps.unfinishedSink2CapsSchem, null, "corz_schem_preview_caps");
      } catch (Throwable e) {
         SchemCorzUtils.handlePipelinesCompile("caps", e);
         return;
      }

      if (slot.gpuBufferHelperCapsSchem != null) {
         slot.gpuBufferHelperCapsSchem.close();
      }

      slot.gpuBufferHelperCapsSchem = gpuBufferHelper;
      slot.capsValue = colorsModelsCaps.capsSchemValue - slot.upload.capsSchemValue;
   }

   @Override
   protected void updateTimeMax() {
      this.lastNanoTimeLoadingSchematic = System.nanoTime();
      this.lastNanoTimeThumbs = this.lastNanoTimeLoadingSchematic;
      thisMeshGpu = this;
   }

   private boolean isLoadingBuilder() {
      String schematicBuilderText = this.schematicBuilder.getStatusText();
      return schematicBuilderText != null && schematicBuilderText.startsWith("Loading ") && schematicBuilderText.endsWith("...");
   }

   @Override
   public boolean canAmountMatrix(ButtonMin buttonMin, double matrixValue, double matrixValue2, double exponent) {
      this.cubeOrbit.handleAmount(exponent);
      PreviewSchematic.Slot gpumeshmap = this.gpuMeshMap.get(this.gpuMeshText);
      if (gpumeshmap != null && gpumeshmap.upload != null && gpumeshmap.cubeScale > 0.0F) {
         float matrixFactor = 1.0F / CorzRoundedUtils.getFramebufferHeight();
         this.matrixAmount = (float)(matrixValue - buttonMin.getCubeAmount()) * matrixFactor / gpumeshmap.cubeScale;
         this.panOffsetY = (float)(-(matrixValue2 - buttonMin.getCubeSchematic())) * matrixFactor / gpumeshmap.cubeScale;
         this.parseActive = true;
      }

      return true;
   }

   protected final boolean isMeshGpu() {
      return this.canFileLoad(this.gpuMeshText);
   }

   protected final boolean isSchematicsTexts() {
      return this.schematicsSelectedTexts.length > 0 || this.schematicBuilder.getLoadedSchematic() != null;
   }

   @Override
   public void renderCubeSchematic(DrawContext context, ButtonMin buttonMin) {
      String formatSchematicsTexts = this.formatSchematicsTexts(buttonMin.selectedIndex);
      PreviewSchematic.Slot gpumeshmap = this.gpuMeshMap.get(formatSchematicsTexts);
      if (gpumeshmap != null && gpumeshmap.gpuBufferHelper != null && gpumeshmap.upload != null) {
         float cubeSchematicValue = gpumeshmap.lastNanoTime == 0L ? Float.MAX_VALUE : (float)(System.nanoTime() - gpumeshmap.lastNanoTime) / 1000000.0F;
         float cubeValue = gpumeshmap.lastNanoTime == 0L ? 1.0F : AmountPreviewUtils.getAmount10(cubeSchematicValue);
         if (cubeValue < 1.0F) {
            this.renderLoadingSchematic(context, buttonMin, 1.0F - cubeValue);
         }

         this.renderAmountCube(context, buttonMin, gpumeshmap, cubeValue, gpumeshmap.lastNanoTime == 0L ? 1.0F : AmountPreviewUtils.getAmountForFloat(cubeSchematicValue));
      } else if (this.schematicsSelectedTexts.length == 0 && this.schematicBuilder.getLoadedSchematic() == null) {
         MatrixStackUtilsRelease.render(
            context,
            buttonMin.cubeKnifeAmount,
            buttonMin.cubeSchematicAmount,
            buttonMin.cubeSchematicAmount2Cube,
            buttonMin.cubeSchematicAmount3Cube,
            IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("folder"),
            "No schematics yet",
            "Add .litematic or .schem files to the schematics folder"
         );
      } else if (formatSchematicsTexts.isEmpty()) {
         MatrixStackUtilsRelease.render(
            context,
            buttonMin.cubeKnifeAmount,
            buttonMin.cubeSchematicAmount,
            buttonMin.cubeSchematicAmount2Cube,
            buttonMin.cubeSchematicAmount3Cube,
            MatrixStackUtilsRelease.getIdentifier("cube"),
            "Pick a schematic",
            "Choose one from the list"
         );
      } else if (gpumeshmap != null && gpumeshmap.upload != null && gpumeshmap.upload.isUnfinishedSink()) {
         MatrixStackUtilsRelease.render(
            context,
            buttonMin.cubeKnifeAmount,
            buttonMin.cubeSchematicAmount,
            buttonMin.cubeSchematicAmount2Cube,
            buttonMin.cubeSchematicAmount3Cube,
            MatrixStackUtilsRelease.getIdentifier("cube"),
            "Nothing to preview",
            "This schematic has no blocks"
         );
      } else if (gpumeshmap != null && gpumeshmap.upload != null && SchemCorzUtils.isPipelinesCompile()) {
         this.renderCubePreview(context, buttonMin);
      } else if (this.canFileLoad(formatSchematicsTexts)) {
         MatrixStackUtilsRelease.render(
            context,
            buttonMin.cubeKnifeAmount,
            buttonMin.cubeSchematicAmount,
            buttonMin.cubeSchematicAmount2Cube,
            buttonMin.cubeSchematicAmount3Cube,
            MatrixStackUtilsRelease.getIdentifier("cross"),
            "Couldn't load this schematic",
            "The file may be damaged or from an unsupported version"
         );
      } else if (gpumeshmap != null && gpumeshmap.couldnUnsupportedFlag) {
         MatrixStackUtilsRelease.render(
            context,
            buttonMin.cubeKnifeAmount,
            buttonMin.cubeSchematicAmount,
            buttonMin.cubeSchematicAmount2Cube,
            buttonMin.cubeSchematicAmount3Cube,
            MatrixStackUtilsRelease.getIdentifier("cube"),
            "Couldn't build the 3D preview",
            "The schematic itself loaded fine"
         );
      } else {
         this.renderLoadingSchematic(context, buttonMin, 1.0F);
      }
   }

   private void handleModFloor(int modFloorValue) {
      int modFloorLimit = this.layerCount;
      if (modFloorLimit > 1 && modFloorValue != 0) {
         int modFloorValue2 = this.floorMod < 0 ? modFloorLimit - 1 : this.floorMod;
         int floorMod = Math.floorMod(modFloorValue2 - Integer.signum(modFloorValue), modFloorLimit);
         this.floorMod = floorMod >= modFloorLimit - 1 ? -1 : floorMod;
      }
   }

   private void renderCubePreview(DrawContext context, ButtonMin buttonMin) {
      MatrixStackUtilsRelease.render(
         context,
         buttonMin.cubeKnifeAmount,
         buttonMin.cubeSchematicAmount,
         buttonMin.cubeSchematicAmount2Cube,
         buttonMin.cubeSchematicAmount3Cube,
         MatrixStackUtilsRelease.getIdentifier("cube"),
         "3D preview unavailable",
         "The preview renderer was turned off after an error"
      );
   }

   public static String getFormat(String previewSchematicText) {
      PreviewSchematic thisMeshGpu2 = thisMeshGpu;
      if (thisMeshGpu2 != null && MinecraftClient.getInstance().currentScreen == thisMeshGpu2) {
         String[] split = previewSchematicText.trim().split("\\s+");

         try {
            String lowerCase = split[0].toLowerCase(Locale.ROOT);
            switch (lowerCase) {
               case "orbit":
                  thisMeshGpu2.updateMinMax();
                  float float2 = Float.parseFloat(split[1]);
                  float previewSchematicValue = split.length > 2 ? Float.parseFloat(split[2]) : 26.0F;
                  thisMeshGpu2.cubeOrbit.updateMaxAmount();
                  thisMeshGpu2.cubeOrbit.handleAmount(-(float2 - thisMeshGpu2.cubeOrbit.getSmoothedYaw()) / 1.6F, (previewSchematicValue - thisMeshGpu2.cubeOrbit.getSmoothedPitch()) / 0.6F);
                  thisMeshGpu2.cubeOrbit.updateMaxFlag();
                  break;
               case "zoom":
                  thisMeshGpu2.cubeOrbit.handleAmount(Float.parseFloat(split[1]));
                  break;
               case "layer":
                  int layerCount2 = thisMeshGpu2.layerCount;
                  int previewSchematicLimit = split[1].equalsIgnoreCase("all") ? -1 : Integer.parseInt(split[1]) - 1;
                  thisMeshGpu2.floorMod = previewSchematicLimit >= 0 && previewSchematicLimit < layerCount2 - 1 ? previewSchematicLimit : -1;
                  break;
               case "pick":
                  int index = Integer.parseInt(split[1]) - 1;
                  if (index >= 0 && index < thisMeshGpu2.schematicsSelectedTexts.length) {
                     thisMeshGpu2.handleMeshText(thisMeshGpu2.schematicsSelectedTexts[index]);
                     thisMeshGpu2.previewRail.handleCrossfadeFlag(thisMeshGpu2.selectedSchematicsValue, true);
                  }
                  break;
               case "pan":
                  thisMeshGpu2.updateMinMax();
                  thisMeshGpu2.parseFloat = Float.parseFloat(split[1]);
                  thisMeshGpu2.floatParse = Float.parseFloat(split[2]);
                  thisMeshGpu2.updateUploadParse();
                  break;
               case "reset":
                  thisMeshGpu2.updateParseMin();
                  break;
               case "spin":
                  thisMeshGpu2.cubeOrbit.getAmount(split.length > 1 && split[1].equalsIgnoreCase("off") ? 0.0F : 9.0F);
                  break;
               default:
                  return "schem: unknown " + previewSchematicText;
            }
         } catch (RuntimeException e) {
            return "schem: bad args " + previewSchematicText;
         }

         PreviewSchematic.Slot gpumeshmap = thisMeshGpu2.gpuMeshMap.get(thisMeshGpu2.gpuMeshText);
         return String.format(
            Locale.ROOT,
            "schem %s -> file=%s mesh=%s layers=%d layer=%d yaw=%.1f pitch=%.1f zoom=%.2f gpu=%s",
            previewSchematicText,
            thisMeshGpu2.gpuMeshText,
            gpumeshmap != null && gpumeshmap.gpuBufferHelper != null,
            gpumeshmap != null && gpumeshmap.upload != null ? gpumeshmap.upload.gpuUploadValue : 0,
            thisMeshGpu2.layersAllValue,
            thisMeshGpu2.cubeOrbit.getSmoothedYaw(),
            thisMeshGpu2.cubeOrbit.getSmoothedPitch(),
            thisMeshGpu2.cubeOrbit.getSmoothedDistance(),
            SchemCorzUtils.isPipelinesCompile() ? "failed" : "ok"
         );
      } else {
         return "schem: preview not open";
      }
   }

   public static String formatTextForString(String previewSchematicText) {
      return formatConvertedCompile(previewSchematicText, true, false);
   }

   private void renderPartialPreview(DrawContext context, ButtonMin buttonMin, boolean partialPreviewFlag) {
      if (this.partialPreviewText != null && this.append != null) {
         float partialPreviewValue = 12.0F;
         float matrixStackValue = MatrixStackUtilsAmount.getAmount(buttonMin.cubeSchematicAmount + 6.0F);
         float partialPreviewValue3 = buttonMin.cubeKnifeAmount + buttonMin.cubeSchematicAmount2Cube - 6.0F;
         float partialPreviewValue4 = getAmount(this.append);
         float partialPreviewValue5 = getAmount(this.partialPreviewText);
         render(context, this.append, partialPreviewValue3 - partialPreviewValue4, matrixStackValue, partialPreviewValue4, partialPreviewValue, -7630437);
         render(context, this.partialPreviewText, partialPreviewValue3 - partialPreviewValue4 - 3.0F - partialPreviewValue5, matrixStackValue, partialPreviewValue5, partialPreviewValue, -7630437);
         if (partialPreviewFlag) {
            float materialFactor = getAmount("Partial preview");
            render(context, "Partial preview", partialPreviewValue3 - partialPreviewValue4 - partialPreviewValue5 - 6.0F - materialFactor, matrixStackValue, materialFactor, partialPreviewValue, -1535700);
         }
      }
   }

   @Override
   public void handleMatrixParse(ButtonMin buttonMin, double matrixParseValue, double minmaxValue, double orbitValue, double rotateQuaternionfValue, int button) {
      if (button != 0) {
         PreviewSchematic.Slot gpumeshmap = this.gpuMeshMap.get(this.gpuMeshText);
         if (gpumeshmap != null && gpumeshmap.upload != null) {
            float matrixParseDivisor = gpumeshmap.cubeScale * this.cubeOrbit.getSmoothedDistance();
            if (!(matrixParseDivisor <= 0.0F)) {
               float matrixParseFactor = 1.0F / CorzRoundedUtils.getFramebufferHeight();
               this.parseFloat += (float)orbitValue * matrixParseFactor / matrixParseDivisor;
               this.floatParse -= (float)rotateQuaternionfValue * matrixParseFactor / matrixParseDivisor;
               this.updateUploadParse();
            }
         }
      } else {
         if (orbitValue != 0.0 || rotateQuaternionfValue != 0.0) {
            this.lastNanoTime = 0L;
         }

         this.cubeOrbit.handleAmount(orbitValue, rotateQuaternionfValue);
      }
   }

   protected final boolean isMeshGpuSchematicBuilder() {
      String loadedSchematicText = this.schematicBuilder.getLoadedFileName();
      return !this.gpuMeshText.isEmpty()
         && this.gpuMeshText.equals(loadedSchematicText)
         && this.schematicBuilder.getLoadedSchematic() != null
         && this.meshText == null
         && !this.isLoadingBuilder();
   }

   @Override
   public void removed() {
      super.removed();
      this.updateMeshGpu();
   }

   private void handleSchematicsSelected(String gpuMeshText) {
      this.previewSchematicText = this.gpuMeshText;
      this.gpuMeshText = gpuMeshText;
      this.selectedSchematicsValue = this.getSchematicsTexts(gpuMeshText);
      this.updateParseMod();
      if (this.selectedSchematicsValue >= 0) {
         this.previewRail.handleCrossfadeFlag(this.selectedSchematicsValue, true);
      }
   }

   private void renderAmountCube(DrawContext context, ButtonMin buttonMin, PreviewSchematic.Slot slot, float cubeValue, float x2) {
      if (!SchemCorzUtils.isPipelinesCompileSchemCorzActive()) {
         this.renderCubePreview(context, buttonMin);
      } else if (!(cubeValue <= 0.002F)) {
         BakedQuadUtils.Result upload = slot.upload;
         float cubeFactor = 1.0F / CorzRoundedUtils.getFramebufferHeight();
         int max = Math.max(1, Math.round(buttonMin.cubeSchematicAmount2Cube * cubeFactor));
         int max2 = Math.max(1, Math.round(buttonMin.cubeSchematicAmount3Cube * cubeFactor));
         if (slot.cachedLayerMax != max || slot.cubeValue != max2) {
            slot.cachedLayerMax = max;
            slot.cubeValue = max2;
            slot.cubeScale = MatrixRotateUtilsAmount2.getAmount(upload.primaryRed, upload.uploadParseAmount, upload.cubeAmount, max, max2, 26.0F);
         }

         boolean equals = slot.cubeText.equals(this.gpuMeshText);
         int cubeLimit = equals ? this.layersAllValue : -1;
         float x = slot.cubeScale * this.cubeOrbit.getSmoothedDistance();
         float rotateTranslateBound = MatrixRotateUtilsAmount2.getAmount(upload.primaryRed, upload.uploadParseAmount, upload.cubeAmount);
         MatrixRotateUtilsAmount2.getRotateTranslate(
            this.cubeMatrix4f,
            upload.secondaryRed,
            upload.cubeScale,
            upload.secondaryBlue,
            rotateTranslateBound,
            this.cubeOrbit.getSmoothedYaw(),
            this.cubeOrbit.getSmoothedPitch(),
            x,
            equals ? this.parseCount : 0.0F,
            equals ? this.uploadParseCount : 0.0F,
            max,
            max2
         );
         GpuBufferHelper gpuBufferHelper = cubeLimit >= 0 && slot.capsValue == cubeLimit ? slot.gpuBufferHelperCapsSchem : null;
         int deviceTexture = SchemCorzUtils.getDeviceTexture(max, max2);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(cubeValue);
         boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, buttonMin.getCubeAmount(), buttonMin.getCubeSchematic(), x2, 0.0F);

         try {
            SchemCorzUtils.handleSchemSampler(
               slot.gpuBufferHelper,
               upload.getUploadCaps(cubeLimit),
               upload.getUnfinishedSink(cubeLimit),
               cubeLimit > 0 ? upload.getUploadCaps(cubeLimit - 1) : 0,
               cubeLimit > 0 ? upload.getUnfinishedSink(cubeLimit - 1) : 0,
               upload.getUnfinishedSinkForInt(cubeLimit),
               cubeLimit > 0 ? upload.getUnfinishedSinkForInt(cubeLimit - 1) : 0,
               gpuBufferHelper,
               this.cubeMatrix4f,
               max * deviceTexture,
               max2 * deviceTexture
            );
            SchemCorzUtils.handleOrderFramebuffer(
               MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix(),
               buttonMin.cubeKnifeAmount,
               buttonMin.cubeSchematicAmount,
               buttonMin.cubeSchematicAmount2Cube,
               buttonMin.cubeSchematicAmount3Cube,
               buttonMin.renderAmount,
               CorzRoundedUtils.getPanelOverlay()
            );
         } catch (Throwable e) {
            SchemCorzUtils.handlePipelinesCompile("render", e);
         } finally {
            MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   @Override
   public float getSchematicMaterialsAmount() {
      return 500.0F;
   }

   @Override
   public void handleFolderDone(PreviewText previewText) {
      if (this.schematicsSelectedTexts.length == 0 && this.schematicBuilder.getLoadedSchematic() == null) {
         previewText.getText("corzclient/schematics").getFlag().getClass073("folder", "Open folder", this.folder);
      }
   }

   public PreviewSchematic(Screen screen, SchematicLoader schematicBuilder) {
      this(screen, schematicBuilder, "Schematic Preview");
   }

   @Override
   public void handleInspectsSchematics(int index2) {
      if (index2 >= 0 && index2 < this.schematicsSelectedTexts.length) {
         this.handleMeshText(this.schematicsSelectedTexts[index2]);
      }
   }

   @Override
   public void handleThumbsAmount(float thumbsValue) {
      long nanoTime = System.nanoTime();
      if (nanoTime - this.lastNanoTimeThumbs > 1500000000L) {
         this.lastNanoTimeThumbs = nanoTime;
         this.updateSchematicsSelectedSchematicBuilder();
      }

      this.cubeOrbit.handleAmountMax(thumbsValue);
      this.handleAmountParse(thumbsValue);
      this.updateTextMesh();
      this.updateUploadUnsupported();
      this.updateModLayers();
      PreviewSchematic.Slot gpumeshmap = this.gpuMeshMap.get(this.gpuMeshText);
      boolean thumbsFlag = gpumeshmap != null && gpumeshmap.gpuBufferHelper != null;
      this.thumbsAmount = AmountPreviewUtils.getAmount(this.thumbsAmount, !thumbsActive && thumbsFlag ? 1.0F : 0.0F, 8.0F, thumbsValue, 0.002F);
   }

   private String formatSchematicsTexts(int index) {
      return this.getShowSchematics() != 0 && index >= 0 && index < this.schematicsSelectedTexts.length ? this.schematicsSelectedTexts[index] : this.gpuMeshText;
   }

   private int getSchematicsTexts(String schematicsText) {
      if (schematicsText != null && !schematicsText.isEmpty()) {
         for (int i = 0; i < this.schematicsSelectedTexts.length; i++) {
            if (this.schematicsSelectedTexts[i].equals(schematicsText)) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private void handleTextsSchematics(List<String> arrayList) {
      this.copyOf = List.copyOf(arrayList);
      int listSize = arrayList.size();
      String[] array = arrayList.toArray(new String[0]);
      String[] strings = new String[listSize];
      HashMap hashMap = new HashMap();

      for (int i = 0; i < listSize; i++) {
         strings[i] = formatText(array[i], true);
         hashMap.merge(strings[i].toLowerCase(Locale.ROOT), 1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
      }

      for (int j = 0; j < listSize; j++) {
         if ((Integer)hashMap.get(strings[j].toLowerCase(Locale.ROOT)) > 1) {
            strings[j] = formatText(array[j], false);
         }
      }

      Integer[] integers = new Integer[listSize];

      for (int k = 0; k < listSize; k++) {
         integers[k] = k;
      }

      Arrays.sort(integers, (index, index2) -> {
         int var4x = String.CASE_INSENSITIVE_ORDER.compare(strings[index], strings[index2]);
         return var4x != 0 ? var4x : array[index].compareTo(array[index2]);
      });
      this.schematicsSelectedTexts = new String[listSize];
      this.schematicsTexts = new String[listSize];

      for (int m = 0; m < listSize; m++) {
         this.schematicsSelectedTexts[m] = array[integers[m]];
         this.schematicsTexts[m] = strings[integers[m]];
      }
   }

   @Override
   public boolean canMatrixStack(ButtonMin buttonMin, double matrixStackValue, double mouseY, int glfwValue) {
      long nanoTime = System.nanoTime();
      if (glfwValue == 0) {
         if (this.lastNanoTime != 0L && nanoTime - this.lastNanoTime < 300000000L) {
            this.lastNanoTime = 0L;
            this.updateParseMin();
            return true;
         } else {
            this.lastNanoTime = nanoTime;
            this.updateMinMax();
            this.cubeOrbit.updateMaxAmount();
            return true;
         }
      } else {
         this.updateMinMax();
         return true;
      }
   }

   @Override
   public boolean canMaxSubstring(int substringValue, int modifiers) {
      switch (substringValue) {
         case 82:
            this.updateParseMin();
            return true;
         case 266:
            this.handleModFloorForInt(1);
            return true;
         case 267:
            this.handleModFloorForInt(-1);
            return true;
         default:
            return false;
      }
   }

   private void updateParseMin() {
      this.cubeOrbit.updateAmountMax();
      this.cubeOrbit.getAmount(9.0F);
      this.parseFloat = this.floatParse = 0.0F;
      this.parseActive = false;
   }

   private void updateSchematicsSelectedSchematicBuilder() {
      try {
         this.schematicBuilder.refreshFolderList();
      } catch (Throwable e) {
      }

      List schematicsFolderItems = this.schematicBuilder.getFolderFileNames();
      if (!this.isEquals(schematicsFolderItems)) {
         int selectedSchematicsValue = this.selectedSchematicsValue;
         this.handleTextsSchematics(schematicsFolderItems);
         this.selectedSchematicsValue = this.getSchematicsTexts(this.gpuMeshText);
         if (this.selectedSchematicsValue >= 0) {
            this.previewRail.handleCrossfadeFlag(this.selectedSchematicsValue, false);
         } else if (this.schematicsSelectedTexts.length > 0 && !this.isClericLimits()) {
            int index = Math.max(0, Math.min(this.schematicsSelectedTexts.length - 1, selectedSchematicsValue));
            this.handleMeshText(this.schematicsSelectedTexts[index]);
            this.previewRail.handleCrossfadeFlag(this.selectedSchematicsValue, true);
         }
      }
   }

   @Override
   public int getShowSchematics() {
      return this.schematicsSelectedTexts.length >= 2 ? this.schematicsSelectedTexts.length : 0;
   }

   private void render(DrawContext context, ButtonMin buttonMin, String previewSchematicText, Identifier id2, float previewSchematicValue, float matrixStackValue, float materialFactor, boolean previewSchematicFlag, Runnable runnable) {
      float previewSchematicLimit = previewSchematicFlag ? buttonMin.getMin(previewSchematicText, previewSchematicValue, matrixStackValue, 12.0F, materialFactor) : 0.0F;
      if (previewSchematicLimit > 0.01F) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, previewSchematicValue + 1.5F, matrixStackValue + 1.5F, 9.0F, materialFactor - 3.0F, 2.5F, MatrixStackUtilsAmount.getValue(-1, 0.07F * previewSchematicLimit));
      }

      MatrixStackUtilsRelease.handle(
         id2,
         MatrixStackUtilsAmount.getAmount(previewSchematicValue + 3.0F),
         MatrixStackUtilsAmount.getAmount(matrixStackValue + (materialFactor - 6.0F) / 2.0F),
         6.0F,
         previewSchematicFlag ? CardMatrixUtils.getValue(-7630437, -920587, previewSchematicLimit) : -11117467
      );
      if (previewSchematicFlag) {
         buttonMin.handle(previewSchematicText, previewSchematicValue, matrixStackValue, 12.0F, materialFactor, runnable);
      }
   }

   private void handleMeshText(String gpuMeshText) {
      if (gpuMeshText != null && !gpuMeshText.equals(this.gpuMeshText)) {
         this.previewSchematicText = this.gpuMeshText;
         this.gpuMeshText = gpuMeshText;
         this.selectedSchematicsValue = this.getSchematicsTexts(gpuMeshText);
         this.updateParseMod();
         String loadedSchematicText = this.schematicBuilder.getLoadedFileName();
         this.meshText = gpuMeshText.equals(loadedSchematicText) && this.schematicBuilder.getLoadedSchematic() != null ? null : gpuMeshText;
      }
   }

   private void renderResetView(DrawContext context, ButtonMin buttonMin, float viewValue, boolean viewFlag) {
      float materialFactor = 13.0F;
      float cubeSchematicValue = 10.0F + (float)Math.ceil(MatrixStackUtilsRelease.getAmount("Reset view", 0.72F)) + 10.0F;
      float buttonValue = MatrixStackUtilsAmount.getAmount(buttonMin.cubeKnifeAmount + buttonMin.cubeSchematicAmount2Cube - 6.0F - cubeSchematicValue);
      float cardMatrixFactor = viewFlag ? buttonMin.getMin("ov:reset", buttonValue, viewValue, cubeSchematicValue, materialFactor) : 0.0F;
      boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, buttonValue + cubeSchematicValue / 2.0F, viewValue + materialFactor / 2.0F, buttonMin.getMin("ov:reset", 0.05F), 0.0F);

      try {
         MatrixStackUtilsRelease.renderReleaseRight(context, buttonValue, viewValue, cubeSchematicValue, materialFactor, 4.0F, cardMatrixFactor, 0.0F);
         float matrixStackValue = buttonValue + 5.0F;
         int releaseRight = MatrixStackUtilsRelease.getReleaseRight(cardMatrixFactor, 0.0F);
         MatrixStackUtilsRelease.handle(
            IcoGenUtils.getIdentifier("rotate-ccw"), matrixStackValue, MatrixStackUtilsAmount.getAmount(viewValue + (materialFactor - 7.0F) / 2.0F), 7.0F, releaseRight
         );
         MatrixStackUtilsRelease.handle("Reset view", matrixStackValue + 7.0F + 3.0F, viewValue, materialFactor, 0.72F, releaseRight);
      } finally {
         MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
      }

      if (viewFlag) {
         buttonMin.handle("ov:reset", buttonValue, viewValue, cubeSchematicValue, materialFactor, this.resetAction);
      }
   }

   private void handleGpuUpload(PreviewSchematic.Slot slot, BakedQuadUtils.Result upload) {
      GpuBufferHelper gpuBufferHelper = null;
      if (!upload.isUnfinishedSink() && SchemCorzUtils.isPipelinesCompileSchemCorzActive()) {
         try {
            gpuBufferHelper = GpuBufferHelper.getVertexClose(upload.upload, upload.unfinishedSink, upload.unfinishedSink2UnfinishedSin, "corz_schem_preview");
         } catch (Throwable e) {
            SchemCorzUtils.handlePipelinesCompile("upload", e);
         }
      }

      upload.upload.releaseVertexData();
      upload.unfinishedSink.releaseVertexData();
      upload.unfinishedSink2UnfinishedSin.releaseVertexData();
      boolean hasGpubufferhelper = slot.gpuBufferHelper != null;
      if (slot.gpuBufferHelper != null) {
         slot.gpuBufferHelper.close();
      }

      if (slot.gpuBufferHelperCapsSchem != null) {
         slot.gpuBufferHelperCapsSchem.close();
      }

      if (slot.threadExecutor != null) {
         slot.threadExecutor.updateFlag();
      }

      slot.gpuBufferHelperCapsSchem = null;
      slot.threadExecutor = null;
      slot.capsValue = -1;
      slot.gpuBufferHelper = gpuBufferHelper;
      slot.upload = upload;
      slot.couldnUnsupportedFlag = false;
      slot.cachedLayerMax = slot.cubeValue = -1;
      slot.lastNanoTime = hasGpubufferhelper ? 0L : System.nanoTime();
      if (slot.cubeText.equals(this.gpuMeshText)) {
         this.layersAllValue = -1;
         if (this.floorMod >= upload.gpuUploadValue - 1) {
            this.floorMod = -1;
         }
      }
   }

   @Override
   public void handleMatrixStack(ButtonMin buttonMin, double matrixStackValue, double mouseReleasedValue, int button) {
      if (button == 0) {
         this.cubeOrbit.updateMaxFlag();
      }
   }

   private void updateMeshGpu() {
      for (PreviewSchematic.Slot slot : this.gpuMeshMap.values()) {
         slot.updateGpuBuffer();
      }

      this.gpuMeshMap.clear();
      SchemCorzUtils.updateCompositeSimple();
      if (thisMeshGpu == this) {
         thisMeshGpu = null;
      }
   }

   private static String formatConvertedCompile(String convertedCompileText, boolean convertedCompileFlag, boolean previewSchematicFlag) {
      String formatText2 = formatText(convertedCompileText == null ? "" : convertedCompileText);
      String compileCommandText = formatText2;

      String moneyEnoughText;
      do {
         moneyEnoughText = compileCommandText;
         compileCommandText = compileCommandText.replaceAll("(?i)[-_ ]+(converted|copy|from-[\\w.-]+)$", "");
      } while (!compileCommandText.equals(moneyEnoughText) && !compileCommandText.isEmpty());

      if (convertedCompileFlag) {
         Matcher matcher = compile.matcher(compileCommandText);
         StringBuilder stringBuilder = new StringBuilder();

         while (matcher.find()) {
            int convertedCompileCounter = 0;

            for (int i = matcher.start(); i < matcher.end(); i++) {
               if (Character.isLetter(compileCommandText.charAt(i))) {
                  convertedCompileCounter++;
               }
            }

            matcher.appendReplacement(stringBuilder, convertedCompileCounter >= 4 ? " " : Matcher.quoteReplacement(matcher.group()));
         }

         matcher.appendTail(stringBuilder);
         if (stringBuilder.toString().trim().length() >= 3) {
            compileCommandText = stringBuilder.toString();
         }
      }

      if (compileCommandText.indexOf(95) >= 0) {
         compileCommandText = compileCommandText.replaceAll("([A-Za-z0-9])_s(_|$)", "$1's ").replace('_', ' ');
      }

      if (compileCommandText.indexOf(32) < 0) {
         compileCommandText = compileCommandText.replace('-', ' ');
      }

      String trim = compileCommandText.replaceAll("\\s+", " ").trim();
      if (previewSchematicFlag) {
         Matcher matcher2 = previewSchematicPattern.matcher(trim);
         if (matcher2.find() && !matcher2.group(1).equalsIgnoreCase(matcher2.group(2)) && !REQUEST_DESIGN_SET.contains(matcher2.group(2).toLowerCase(Locale.ROOT))) {
            String trim2 = trim.substring(0, matcher2.end(1)).trim();
            if (trim2.length() >= 3) {
               trim = trim2;
            }
         }
      }

      int index = 0;
      int varLength = trim.length();

      while (index < varLength && " -_+&\u00b7.,".indexOf(trim.charAt(index)) >= 0) {
         index++;
      }

      while (varLength > index && " -_+&\u00b7.,".indexOf(trim.charAt(varLength - 1)) >= 0) {
         varLength--;
      }

      String substring = trim.substring(index, varLength);
      if (substring.isEmpty()) {
         return formatText2;
      } else {
         boolean upperCase3 = false;

         for (int j = 0; j < substring.length() && !upperCase3; j++) {
            upperCase3 = Character.isUpperCase(substring.charAt(j));
         }

         return upperCase3 ? substring : Character.toUpperCase(substring.charAt(0)) + substring.substring(1);
      }
   }

   protected PreviewSchematic(Screen screen, SchematicLoader schematicBuilder, String previewSchematicText) {
      super(screen, previewSchematicText);
      this.cubeMatrix4f = new Matrix4f();
      this.gpuMeshMap = new LinkedHashMap<>();
      this.intConsumer = this::handleModFloorForInt;
      this.chevronLnextIntConsumer = this::handleModFloor;
      this.resetAction = this::updateParseMin;
      this.chevronLnextAction = () -> this.handleModFloorForInt(-1);
      this.lnextChevronAction = () -> this.handleModFloorForInt(1);
      this.folder = this::updateBuilderSchematic;
      this.schematicsSelectedTexts = new String[0];
      this.schematicsTexts = new String[0];
      this.copyOf = List.of();
      this.gpuMeshText = "";
      this.previewSchematicText = "";
      this.layersText = "";
      this.selectedSchematicsValue = -1;
      this.layersAllAmount = -1.0F;
      this.floorMod = -1;
      this.layersAllValue = -1;
      this.lastLayersMin = Integer.MIN_VALUE;
      this.lastLayersMax = -1;
      this.layersAllText = "All layers";
      this.previewSchematicValue = Integer.MIN_VALUE;
      this.schematicsValue = -1;
      this.dragPanAmount = -1.0F;
      this.loadingSchematicAmount = -1.0F;
      this.layersAllValueChevronLnext = -1;
      this.schematicBuilder = schematicBuilder;
      this.handleTextsSchematics(schematicBuilder.getFolderFileNames());
      String layersText = schematicBuilder.getLoadedFileName() == null ? "" : schematicBuilder.getLoadedFileName();
      this.layersText = layersText;
      this.gpuMeshText = !layersText.isEmpty() ? layersText : (this.schematicsSelectedTexts.length > 0 ? this.schematicsSelectedTexts[0] : "");
      this.selectedSchematicsValue = this.getSchematicsTexts(this.gpuMeshText);
      if (this.selectedSchematicsValue >= 0) {
         this.previewRail.handleCrossfadeFlag(this.selectedSchematicsValue, false);
      }

      if (!this.gpuMeshText.isEmpty() && (!this.gpuMeshText.equals(layersText) || schematicBuilder.getLoadedSchematic() == null)) {
         this.meshText = this.gpuMeshText;
      }
   }

   private void updateTextMesh() {
      String layersText = this.schematicBuilder.getLoadedFileName() == null ? "" : this.schematicBuilder.getLoadedFileName();
      ElseBuilder loadedSchematic = this.schematicBuilder.getLoadedSchematic();
      boolean loadingBuilder = this.isLoadingBuilder();
      boolean clericLimits = this.isClericLimits();
      if (this.meshText == null && !loadingBuilder && !clericLimits && layersText.isEmpty() && loadedSchematic != null && this.selectedSchematicsValue >= 0 && !this.canFileLoad(this.gpuMeshText)) {
         this.meshText = this.gpuMeshText;
      }

      if (this.meshText != null && !loadingBuilder && !clericLimits) {
         String meshText = this.meshText;
         this.meshText = null;
         if (!meshText.equals(layersText) || loadedSchematic == null) {
            this.schematicBuilder.loadSchematicAsync(meshText);
            loadingBuilder = true;
         }
      }

      if (!layersText.equals(this.layersText)) {
         this.layersText = layersText;
         if (!layersText.isEmpty() && !layersText.equals(this.gpuMeshText) && this.meshText == null && !loadingBuilder) {
            this.handleSchematicsSelected(layersText);
         } else if (layersText.isEmpty() && loadedSchematic == null && this.meshText == null && !loadingBuilder && !this.gpuMeshText.isEmpty() && !this.canFileLoad(this.gpuMeshText)) {
            this.previewSchematicText = this.gpuMeshText;
            this.gpuMeshText = "";
            this.selectedSchematicsValue = -1;
            this.previewRail.updateMaxFlag();
         }
      }

      if (loadedSchematic != null && this.gpuMeshText.equals(layersText)) {
         this.handleUnsupportedElse(this.getMeshGpu(this.gpuMeshText), loadedSchematic);
      }
   }

   private void handleDragPan(ButtonMin buttonMin, float dragPanValue) {
      if (!(this.thumbsAmount <= 0.004F)) {
         if (this.dragPanAmount < 0.0F) {
            this.dragPanAmount = MatrixStackUtilsRelease.getAmount("Drag to rotate  \u00b7  Scroll to zoom  \u00b7  Right-drag to pan", 0.72F);
         }

         if (!(this.dragPanAmount > buttonMin.cubeSchematicAmount2Cube - 16.0F)) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.thumbsAmount);

            try {
               MatrixStackUtilsRelease.handle(
                  "Drag to rotate  \u00b7  Scroll to zoom  \u00b7  Right-drag to pan",
                  MatrixStackUtilsAmount.getAmount(buttonMin.getCubeAmount() - this.dragPanAmount / 2.0F),
                  dragPanValue,
                  10.0F,
                  0.72F,
                  -7630437
               );
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      }
   }

   private String formatLayersAll() {
      int max = Math.max(1, this.layerCount);
      int min = this.floorMod < 0 ? max - 1 : Math.min(this.floorMod, max - 1);
      if (min != this.lastLayersMin || max != this.lastLayersMax) {
         this.lastLayersMin = min;
         this.lastLayersMax = max;
         this.layersAllText = min >= max - 1 ? "All layers" : "Layer " + (min + 1) + " of " + max;
      }

      return this.layersAllText;
   }

   @Override
   public void renderCubeAmountForDrawContextButtonMin(DrawContext context, ButtonMin buttonMin) {
      TextureImageUtils.renderBackdropZoom(
         context, 0, buttonMin.cubeKnifeAmount, buttonMin.cubeSchematicAmount, buttonMin.cubeSchematicAmount2Cube, buttonMin.cubeSchematicAmount3Cube, buttonMin.renderAmount
      );
      float cubeAmount = buttonMin.getCubeAmount();
      float cubeValue = buttonMin.getCubeSchematic() - buttonMin.cubeSchematicAmount3Cube * 0.03F;
      float cubeBound = buttonMin.cubeSchematicAmount2Cube * 0.34F;
      float hashHuntBound = buttonMin.cubeSchematicAmount3Cube * 0.34F;
      MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
         context,
         cubeAmount - cubeBound / 2.0F,
         cubeValue - hashHuntBound / 2.0F,
         cubeBound,
         hashHuntBound,
         Math.min(cubeBound, hashHuntBound) / 2.0F,
         CardMatrixUtils.getReleaseRight(0.07),
         Math.min(buttonMin.cubeSchematicAmount2Cube, buttonMin.cubeSchematicAmount3Cube) * 0.22F,
         1.0F
      );
   }

   private void handleAmountParse(float parseValue) {
      float layersAllAmount = this.cubeOrbit.getSmoothedDistance();
      if (this.parseActive && this.layersAllAmount > 0.0F && layersAllAmount != this.layersAllAmount) {
         float parseFactor = 1.0F / layersAllAmount - 1.0F / this.layersAllAmount;
         float parseValue2 = this.matrixAmount * parseFactor;
         float parseValue3 = this.panOffsetY * parseFactor;
         this.parseCount += parseValue2;
         this.uploadParseCount += parseValue3;
         this.parseFloat += parseValue2;
         this.floatParse += parseValue3;
         this.updateUploadParse();
      }

      if (layersAllAmount == this.cubeOrbit.getAmount()) {
         this.parseActive = false;
      }

      this.layersAllAmount = layersAllAmount;
      this.parseCount = AmountPreviewUtils.getAmount(this.parseCount, this.parseFloat, 20.0F, parseValue, 1.0E-4F);
      this.uploadParseCount = AmountPreviewUtils.getAmount(this.uploadParseCount, this.floatParse, 20.0F, parseValue, 1.0E-4F);
   }

   protected final String formatSchematicsSelected() {
      return this.selectedSchematicsValue >= 0 && this.selectedSchematicsValue < this.schematicsTexts.length ? this.schematicsTexts[this.selectedSchematicsValue] : "";
   }

   private boolean isEquals(List<String> schematicsFolderItems) {
      return schematicsFolderItems.equals(this.copyOf);
   }

   @Override
   public void renderCubeAmount(DrawContext context, ButtonMin buttonMin) {
      PreviewSchematic.Slot gpumeshmap = this.gpuMeshMap.get(this.gpuMeshText);
      boolean cubeFlag = gpumeshmap != null && gpumeshmap.gpuBufferHelper != null && gpumeshmap.upload != null && this.isMeshGpuSchematicBuilder();
      float cubeValue = MatrixStackUtilsAmount.getAmount(buttonMin.cubeSchematicAmount + buttonMin.cubeSchematicAmount3Cube - 6.0F - 13.0F);
      if (cubeFlag) {
         float previewValue = gpumeshmap.lastNanoTime == 0L ? Float.MAX_VALUE : (float)(System.nanoTime() - gpumeshmap.lastNanoTime) / 1000000.0F;
         float cubeLimit = gpumeshmap.lastNanoTime == 0L ? 1.0F : AmountPreviewUtils.getAmount10(previewValue);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(cubeLimit);

         try {
            this.updateElseBuilder();
            this.renderPartialPreview(context, buttonMin, gpumeshmap.upload.cubeActive);
            this.renderChevronLnext(context, buttonMin, MatrixStackUtilsAmount.getAmount(buttonMin.cubeKnifeAmount + 6.0F), cubeValue, 13.0F, cubeLimit > 0.5F);
            this.renderResetView(context, buttonMin, cubeValue, cubeLimit > 0.5F);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }

      this.handleDragPan(buttonMin, cubeFlag ? cubeValue - 13.0F : buttonMin.cubeSchematicAmount + buttonMin.cubeSchematicAmount3Cube - 20.0F);
   }

   private void handleUnsupportedElse(PreviewSchematic.Slot slot, ElseBuilder elseBuilder) {
      if (slot.else2UnsupportedElse != elseBuilder) {
         if (slot.threadExecutorUnsupportedEls != null) {
            slot.threadExecutorUnsupportedEls.updateFlag();
         }

         slot.else2UnsupportedElse = elseBuilder;
         slot.couldnUnsupportedFlag = false;

         try {
            slot.threadExecutorUnsupportedEls = BakedQuadUtils.getThreadExecutor(elseBuilder, BakedQuadUtils.Env.getDefaultColors());
         } catch (Throwable e) {
            slot.threadExecutorUnsupportedEls = null;
            slot.couldnUnsupportedFlag = true;
         }
      }
   }

   private boolean canFileLoad(String fileLoadText) {
      if (fileLoadText != null && !fileLoadText.isEmpty()) {
         String schematicBuilderText = this.schematicBuilder.getStatusText();
         return schematicBuilderText != null && schematicBuilderText.endsWith(fileLoadText)
            ? schematicBuilderText.startsWith("Failed to load ") && schematicBuilderText.length() == "Failed to load ".length() + fileLoadText.length()
               || schematicBuilderText.startsWith("File missing: ") && schematicBuilderText.length() == "File missing: ".length() + fileLoadText.length()
            : false;
      } else {
         return false;
      }
   }

   private void updateModLayers() {
      PreviewSchematic.Slot gpumeshmap = this.gpuMeshMap.get(this.gpuMeshText);
      if (gpumeshmap != null && gpumeshmap.upload != null && gpumeshmap.gpuBufferHelper != null) {
         int gpuUploadValue = gpumeshmap.upload.gpuUploadValue;
         if (this.floorMod >= gpuUploadValue - 1) {
            this.floorMod = -1;
         }

         if (this.floorMod < 0) {
            this.layersAllValue = -1;
         } else if (gpumeshmap.capsValue == this.floorMod) {
            this.layersAllValue = this.floorMod;
         } else {
            if (this.layersAllValue >= 0 && gpumeshmap.capsValue != this.layersAllValue) {
               this.layersAllValue = -1;
            }

            int modLayersValue = gpumeshmap.upload.capsSchemValue + this.floorMod;
            if (gpumeshmap.threadExecutor == null || gpumeshmap.threadExecutor.getValue() != modLayersValue) {
               if (gpumeshmap.threadExecutor != null) {
                  gpumeshmap.threadExecutor.updateFlag();
               }

               try {
                  gpumeshmap.threadExecutor = BakedQuadUtils.getThreadExecutor(gpumeshmap.upload.arrayMax, modLayersValue);
               } catch (Throwable e) {
                  gpumeshmap.threadExecutor = null;
               }
            }
         }
      } else {
         this.layersAllValue = -1;
      }
   }

   @Override
   public String formatSchematicsText() {
      if (!this.gpuMeshText.equals(this.schematicsText)
         || this.selectedSchematicsValue != this.previewSchematicValue
         || this.schematicsSelectedTexts.length != this.schematicsValue) {
         this.schematicsText = this.gpuMeshText;
         this.previewSchematicValue = this.selectedSchematicsValue;
         this.schematicsValue = this.schematicsSelectedTexts.length;
         ElseBuilder loadedSchematic = this.schematicBuilder.getLoadedSchematic();
         String schematicsText;
         if (!this.gpuMeshText.isEmpty()) {
            schematicsText = formatText(this.gpuMeshText);
         } else if (loadedSchematic != null && loadedSchematic.orElse != null && !loadedSchematic.orElse.isBlank()) {
            schematicsText = loadedSchematic.orElse;
         } else {
            schematicsText = this.schematicsSelectedTexts.length == 0 ? "No schematics found" : "No schematic selected";
         }

         this.schematicsSelectedText = this.selectedSchematicsValue >= 0 && this.schematicsSelectedTexts.length >= 2
            ? schematicsText + "  \u00b7  " + (this.selectedSchematicsValue + 1) + " of " + this.schematicsSelectedTexts.length
            : schematicsText;
      }

      return this.schematicsSelectedText;
   }

   private void renderLoadingSchematic(DrawContext context, ButtonMin buttonMin, float loadingSchematicValue) {
      if (!(loadingSchematicValue <= 0.002F)) {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(loadingSchematicValue);

         try {
            float seconds = (float)(System.nanoTime() - this.lastNanoTimeLoadingSchematic) / 1.0E9F;
            float cubeAmount = buttonMin.getCubeAmount();
            float loadingSchematicValue2 = buttonMin.getCubeSchematic() - 6.0F;

            for (int i = 0; i < 3; i++) {
               float loadingSchematicValue3 = 0.5F + 0.5F * (float)Math.sin(seconds * 1.1F * Math.PI * 2.0 - i * 0.7);
               float matrixPeekValue = cubeAmount + (i - 1) * 11.0F - 2.0F;
               float configsLikeValue = loadingSchematicValue2 - 2.0F - 1.5F * loadingSchematicValue3;
               MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, configsLikeValue, 4.0F, 4.0F, 2.0F, CardMatrixUtils.getReleaseRight(0.3 + 0.7 * loadingSchematicValue3));
            }

            if (this.loadingSchematicAmount < 0.0F) {
               this.loadingSchematicAmount = MatrixStackUtilsRelease.getAmount("Loading schematic", 0.82F);
            }

            MatrixStackUtilsRelease.handle(
               "Loading schematic", MatrixStackUtilsAmount.getAmount(cubeAmount - this.loadingSchematicAmount / 2.0F), loadingSchematicValue2 + 8.0F, 10.0F, 0.82F, -7630437
            );
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private void updateUploadParse() {
      PreviewSchematic.Slot gpumeshmap = this.gpuMeshMap.get(this.gpuMeshText);
      if (gpumeshmap != null && gpumeshmap.upload != null) {
         float uploadParseValue = MatrixRotateUtilsAmount2.getAmount(gpumeshmap.upload.primaryRed, gpumeshmap.upload.uploadParseAmount, gpumeshmap.upload.cubeAmount);
         this.parseFloat = MatrixRotateUtilsAmount2.getAmount(this.parseFloat, uploadParseValue);
         this.floatParse = MatrixRotateUtilsAmount2.getAmount(this.floatParse, uploadParseValue);
         this.parseCount = MatrixRotateUtilsAmount2.getAmount(this.parseCount, uploadParseValue);
         this.uploadParseCount = MatrixRotateUtilsAmount2.getAmount(this.uploadParseCount, uploadParseValue);
      }
   }

   private void updateUploadUnsupported() {
      for (PreviewSchematic.Slot slot : this.gpuMeshMap.values()) {
         BakedQuadUtils.BuildJob threadExecutorUnsupportedEls = slot.threadExecutorUnsupportedEls;
         if (threadExecutorUnsupportedEls != null && threadExecutorUnsupportedEls.isFlag()) {
            slot.threadExecutorUnsupportedEls = null;
            if (!threadExecutorUnsupportedEls.isElseArrayActive()) {
               BakedQuadUtils.Result upload = threadExecutorUnsupportedEls.getUpload();
               if (threadExecutorUnsupportedEls.getThrowable() == null && upload != null) {
                  this.handleGpuUpload(slot, upload);
               } else {
                  slot.couldnUnsupportedFlag = true;
                  if (threadExecutorUnsupportedEls.getThrowable() != null) {
                  }
               }
            }
         }

         BakedQuadUtils.CapsJob threadExecutor = slot.threadExecutor;
         if (threadExecutor != null && threadExecutor.isFlag()) {
            slot.threadExecutor = null;
            if (!threadExecutor.isElseArrayActive() && threadExecutor.getColorsModelsCaps() != null && slot.upload != null) {
               this.handleCapsSchem(slot, threadExecutor);
            }
         }
      }
   }

   private void updateElseBuilder() {
      String elseBuilderText = this.schematicBuilder.getLoadedFileName() == null ? "" : this.schematicBuilder.getLoadedFileName();
      ElseBuilder loadedSchematic = this.schematicBuilder.getLoadedSchematic();
      ElseBuilder else22 = loadedSchematic != null && this.gpuMeshText.equals(elseBuilderText) ? loadedSchematic : null;
      if (else22 != null && else22 != this.activeBuilderEntry) {
         this.activeBuilderEntry = else22;
         this.partialPreviewText = else22.orElse2BuilderElse + " \u00d7 " + else22.orElse3BuilderElse + " \u00d7 " + else22.orElse4BuilderElse;
         this.append = String.format(Locale.ROOT, "%,d", else22.builderAutoValue) + (else22.builderAutoValue == 1 ? " block" : " blocks");
      }

      PreviewSchematic.Slot gpumeshmap = this.gpuMeshMap.get(this.gpuMeshText);
      if (gpumeshmap != null && gpumeshmap.upload != null) {
         this.layerCount = gpumeshmap.upload.gpuUploadValue;
      } else if (this.activeBuilderEntry != null) {
         this.layerCount = this.activeBuilderEntry.orElse3BuilderElse;
      }
   }

   @Override
   public String formatTextsInspects(int index2) {
      return index2 >= 0 && index2 < this.schematicsTexts.length ? this.schematicsTexts[index2] : "";
   }

   private void updateParseMod() {
      this.parseCount = this.uploadParseCount = this.parseFloat = this.floatParse = 0.0F;
      this.parseActive = false;
      this.floorMod = -1;
      this.layersAllValue = -1;
   }

   private void updateBuilderSchematic() {
      this.schematicBuilder.openSchematicsFolder();
   }

   protected final void handleSchematicsSelected(int substringLimit) {
      if (substringLimit >= 0 && substringLimit < this.schematicsSelectedTexts.length) {
         this.handleMeshText(this.schematicsSelectedTexts[substringLimit]);
         this.previewRail.handleCrossfadeFlag(this.selectedSchematicsValue, true);
      }
   }

   @Override
   public float getAmount() {
      return 340.0F;
   }

   protected final String getGpuMeshText() {
      return this.gpuMeshText;
   }

   private PreviewSchematic.Slot getMeshGpu(String meshGpuText) {
      PreviewSchematic.Slot gpumeshmap = this.gpuMeshMap.get(meshGpuText);
      if (gpumeshmap != null) {
         return gpumeshmap;
      } else {
         if (this.gpuMeshMap.size() >= 2) {
            Iterator iterator = this.gpuMeshMap.entrySet().iterator();

            while (iterator.hasNext() && this.gpuMeshMap.size() >= 2) {
               Entry entry = (Entry)iterator.next();
               if (!((String)entry.getKey()).equals(this.gpuMeshText) && !((String)entry.getKey()).equals(this.previewSchematicText)) {
                  ((PreviewSchematic.Slot)entry.getValue()).updateGpuBuffer();
                  iterator.remove();
               }
            }

            if (this.gpuMeshMap.size() >= 2) {
               Iterator iterator2 = this.gpuMeshMap.entrySet().iterator();

               while (iterator2.hasNext() && this.gpuMeshMap.size() >= 2) {
                  Entry next2 = (Entry)iterator2.next();
                  if (!((String)next2.getKey()).equals(this.gpuMeshText)) {
                     ((PreviewSchematic.Slot)next2.getValue()).updateGpuBuffer();
                     iterator2.remove();
                  }
               }
            }
         }

         PreviewSchematic.Slot slot = new PreviewSchematic.Slot(meshGpuText);
         this.gpuMeshMap.put(meshGpuText, slot);
         return slot;
      }
   }

   protected final void updateMeshText() {
      this.meshText = null;
      if (!this.gpuMeshText.isEmpty()) {
         this.previewSchematicText = this.gpuMeshText;
         this.gpuMeshText = "";
         this.selectedSchematicsValue = -1;
         this.previewRail.updateMaxFlag();
      }
   }

   public static String formatTextString(String previewSchematicText) {
      return formatText(previewSchematicText, true);
   }

   private static String formatText(String previewSchematicText, boolean previewSchematicFlag) {
      return formatConvertedCompile(previewSchematicText, previewSchematicFlag, previewSchematicFlag);
   }

   private void handleModFloorForInt(int modFloorValue) {
      int modFloorLimit = this.layerCount;
      if (modFloorLimit > 1 && modFloorValue != 0) {
         int modFloorValue2 = this.floorMod < 0 ? modFloorLimit - 1 : this.floorMod;
         int max2 = Math.max(0, Math.min(modFloorLimit - 1, modFloorValue2 + Integer.signum(modFloorValue)));
         this.floorMod = max2 >= modFloorLimit - 1 ? -1 : max2;
      }
   }

   private static final class Slot {
      BakedQuadUtils.Result upload;
      boolean couldnUnsupportedFlag;
      final String cubeText;
      ElseBuilder else2UnsupportedElse;
      GpuBufferHelper gpuBufferHelper;
      BakedQuadUtils.CapsJob threadExecutor;
      long lastNanoTime;
      int cachedLayerMax;
      int capsValue = -1;
      int cubeValue;
      BakedQuadUtils.BuildJob threadExecutorUnsupportedEls;
      GpuBufferHelper gpuBufferHelperCapsSchem;
      float cubeScale;

      void updateGpuBuffer() {
         if (this.threadExecutorUnsupportedEls != null) {
            this.threadExecutorUnsupportedEls.updateFlag();
         }

         this.threadExecutorUnsupportedEls = null;
         if (this.threadExecutor != null) {
            this.threadExecutor.updateFlag();
         }

         this.threadExecutor = null;
         if (this.gpuBufferHelper != null) {
            this.gpuBufferHelper.close();
         }

         this.gpuBufferHelper = null;
         if (this.gpuBufferHelperCapsSchem != null) {
            this.gpuBufferHelperCapsSchem.close();
         }

         this.gpuBufferHelperCapsSchem = null;
         this.capsValue = -1;
         this.upload = null;
         this.else2UnsupportedElse = null;
      }

      Slot(String text) {
         this.cachedLayerMax = -1;
         this.cubeValue = -1;
         this.cubeScale = 1.0F;
         this.cubeText = text;
      }
   }
}
