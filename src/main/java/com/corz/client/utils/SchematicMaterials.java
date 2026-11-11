package com.corz.client.utils;

import com.corz.client.gui.ButtonMin;
import com.corz.client.gui.GuiClickApi;
import com.corz.client.gui.GuiClickScreen;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.MatrixStackUtilsRelease;
import com.corz.client.gui.PreviewText;
import com.corz.client.inventory.DrawMatricesUtils;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.utils.BuyerAppendUtils.Result;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.joml.Matrix3x2fStack;
import com.corz.client.utils.MaxRamp;
import com.corz.client.utils.HashPopUtils;

public class SchematicMaterials extends GuiClickScreen {
   private int lastThumbsCount;
   private float priceRevealProgress;
   private final boolean estimatedFilterFlag;
   private static final int schematicMaterialsValue = CardMatrixUtils.getValue(-7630437, -8393375, 0.45);
   private float thumbsAmount;
   private String searchText;
   private final Runnable done;
   private long lastRenderNanos;
   private String cheapestNoneText;
   private final float schematicMaterialsAmount;
   private float cachedPanelX;
   private boolean priceFlag;
   private static final String[] totalPlacedTexts = new String[]{"QTY", "UNIT", "COST"};
   private float matrixCount;
   private final MaxMax2 scrollAnimator;
   private final float[] priceAmounts;
   private final float[] schematicMaterialsTable;
   private String append;
   private final List<SchematicMaterials.Line> materialsMatchItems = new ArrayList<>();
   private final float panelHeight;
   private long cheapestLiveTime;
   private final Map<Item, SchematicMaterials.Line> costMaterialsMap = new HashMap<>();
   private boolean priceEstimatedActive;
   private String searchTextLower;
   private static final int SCHEMATIC_MATERIALS_STEP = 1;
   private static boolean harbourLighthouseFlag;
   private float cubeAmount;
   private long nanoTimeCheapestLive;
   private float rowTextX;
   private final float[] priceEstimatedTable;
   private static final String[] missingHotbarTexts = new String[101];
   private float cachedPanelWidth;
   private final String[] materialsMissingTexts;
   private String cube;
   private final MaxRamp cheapestLiveBounds;
   private Result<Item> cheapestLiveResult;
   private float priceTextWidth;
   private static final String[] estimatedPlacedTexts = new String[]{"TOTAL", "PLACED", "LEFT", "HOTBAR", "INV", "MISSING"};
   private float rowRightX;
   private float firstColumnX;
   private float secondColumnX;
   private String cheapestLiveText;
   private final Runnable doneSearchAction;
   private static final int MATERIAL_DIM_COLOR = MatrixStackUtilsAmount.getValue(-920587, 0.55F);
   private final String placedEstimatedText;
   private final int[] cheapestNoneValues;
   private float lastButtonWidth;
   private float substringAmount;
   private float emptyStateAlpha;
   private static final String DONE = "Done";
   private String materialText;
   private SchematicMaterials.Filter filter;
   private String typesNoneText;
   private final SchematicMaterials.Swap[] cheapestLiveSwaps;
   private boolean cheapestLiveActive;
   private float estimatedAmount;
   private float listRightEdge;
   private String materialsCoveredText;
   private String schematicMaterialsText;
   private float priceAmount;
   private float listTopY;
   private final SchematicMaterials.Swap typesNoneSwap;
   private static final int missingPriceValue = CardMatrixUtils.getValue(-7630437, -1535700, 0.55);
   private float headerLineY;
   private float searchBarY;
   private long materialValue;
   private final List<SchematicMaterials.Line> materialsCostItems = new ArrayList<>();
   private static final String PRICE = "No price";
   private static final String MATERIAL = "MATERIAL";
   private float cachedCheapestBound;
   private long cheapestLiveValue;
   private String appendMaterial;
   private float materialAmount;
   private final String materialsTrimText;
   private static final int ESTIMATED_FILTER_COLOR = CardMatrixUtils.getValue(-920587, -7630437, 0.55);
   private static final int ESTIMATED_FILTER_DIM_COLOR = MatrixStackUtilsAmount.getValue(SchematicMaterials.ESTIMATED_FILTER_COLOR, 0.55F);
   private int materialsMissingValue;
   private String search;
   private static final int typesMissingValue = CardMatrixUtils.getValue(-7630437, -1750963, 0.65);
   private long nanoTimeMaterial;
   private MaxMaterialsUtils.Plan<Item> plan2TypesNone;
   private final float[] thumbsTable;
   private final String[] estimatedMaterialTexts;
   private int thumbsCount;
   private final IntConsumer intConsumer;
   private final float[] estimatedTable;
   private final int materialEstimatedValue;
   private float priceColumnX;
   private float lastColumnX;
   private float lastButtonX;

   public void handleFilterEstimated(MaxMaterialsUtils.Plan<Item> plan) {
      if (!this.estimatedFilterFlag && plan != null) {
         this.handleTypesNone(plan, this.previewRail.isFlag());
      }
   }

   @Override
   public void renderCubeSchematic(DrawContext context, ButtonMin buttonMin) {
      long nanoTime = System.nanoTime();
      boolean cubeSchematicFlag = this.lastThumbsCount != this.thumbsCount;
      this.lastThumbsCount = this.thumbsCount;
      this.handleAmountCube(buttonMin);
      if (this.priceFlag) {
         this.updatePriceEstimated();
      }

      this.updateAmountsEstimated();
      this.renderAmountMaterial(context, nanoTime);
      this.renderMaterialAmount(context, buttonMin, cubeSchematicFlag);
      this.renderCubeAmount(context, buttonMin, nanoTime, cubeSchematicFlag);
   }

   public SchematicMaterials(Screen screen, MaxMaterialsUtils.Plan<Item> plan, String schematicMaterialsText, Result<Item> result) {
      super(screen, result != null ? "Build Cost" : "Schematic Materials");
      this.scrollAnimator = new MaxMax2();
      this.cheapestLiveBounds = new MaxRamp(0L);
      this.cheapestLiveSwaps = new SchematicMaterials.Swap[]{new SchematicMaterials.Swap(), new SchematicMaterials.Swap(), new SchematicMaterials.Swap()};
      this.typesNoneSwap = new SchematicMaterials.Swap();
      this.cheapestNoneValues = new int[]{-5854026, -5854026, -5854026};
      this.estimatedTable = new float[6];
      this.priceEstimatedTable = new float[6];
      this.priceAmounts = new float[6];
      this.thumbsTable = new float[7];
      this.schematicMaterialsTable = new float[7];
      this.done = () -> this.updateRailPreview();
      this.doneSearchAction = this::refreshDerivedState;
      this.intConsumer = this::handleFilterMax;
      this.filter = SchematicMaterials.Filter.ALL;
      this.searchText = "";
      this.searchTextLower = "";
      this.cheapestLiveText = "";
      this.materialText = "";
      this.appendMaterial = "";
      this.lastRenderNanos = Long.MIN_VALUE;
      this.materialValue = Long.MIN_VALUE;
      this.nanoTimeMaterial = System.nanoTime();
      this.priceFlag = true;
      this.lastThumbsCount = -1;
      this.cheapestLiveTime = -1L;
      this.cheapestLiveValue = Long.MIN_VALUE;
      this.cachedCheapestBound = -1.0F;
      this.lastButtonX = Float.NaN;
      this.estimatedFilterFlag = result != null;
      this.materialsTrimText = schematicMaterialsText != null && !schematicMaterialsText.isBlank() ? schematicMaterialsText.trim() : null;
      this.materialEstimatedValue = this.estimatedFilterFlag ? totalPlacedTexts.length : estimatedPlacedTexts.length;
      this.estimatedMaterialTexts = this.estimatedFilterFlag ? totalPlacedTexts : estimatedPlacedTexts;
      this.placedEstimatedText = this.estimatedFilterFlag ? "ESTIMATED TOTAL" : "PLACED";
      this.materialsMissingTexts = this.estimatedFilterFlag
         ? new String[]{"Priced", "No price", "Most expensive"}
         : new String[]{"Remaining", "Missing", "Materials"};
      this.materialsMissingValue = this.estimatedFilterFlag ? 2 : 2;
      this.plan2TypesNone = plan;
      if (this.estimatedFilterFlag) {
         this.handleCheapestLive(result, false);
      } else {
         this.handleTypesNone(plan, false);
      }

      int materialscostitemsSize = this.materialsCostItems.size();
      int schematicMaterialsBound = screen instanceof GuiClickApi ? 7 : 9;
      int schematicMaterialsValue = materialscostitemsSize == 0 ? 3 : Math.max(1, Math.min(schematicMaterialsBound, materialscostitemsSize));
      this.schematicMaterialsAmount = this.estimatedFilterFlag ? 420.0F : 480.0F;
      this.panelHeight = 166.0F + (this.estimatedFilterFlag ? 0.0F : 6.0F) + schematicMaterialsValue * 20.0F;
      this.emptyStateAlpha = this.materialsCostItems.isEmpty() ? 1.0F : 0.0F;
      this.priceRevealProgress = this.estimatedFilterFlag && BuyerAppendUtils.canFlag(this.cheapestLiveBounds.getSecondaryMaxBound()) ? 1.0F : 0.0F;
      this.thumbsTable[this.materialsMissingValue + 1] = 1.0F;
   }

   private void handleTextPrice(float priceValue, float priceValue2, long priceValue3) {
      if (this.append != null && !this.append.isEmpty()) {
         float millis = (float)(priceValue3 - this.nanoTimeCheapestLive) / 1000000.0F;
         float panelOverlayLimit;
         if (millis < 180.0F) {
            panelOverlayLimit = AmountPreviewUtils.getAmount6(millis / 180.0F);
         } else if (millis < 2780.0F) {
            panelOverlayLimit = 1.0F;
         } else {
            panelOverlayLimit = 1.0F - AmountPreviewUtils.getAmount8((millis - 180.0F - 2600.0F) / 500.0F);
         }

         if (panelOverlayLimit <= 0.002F && millis > 180.0F) {
            this.append = null;
         } else {
            float priceValue5 = millis < 180.0F ? 3.0F * (1.0F - panelOverlayLimit) : 0.0F;
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

            try {
               int hashOpenValue = this.cheapestLiveActive ? missingPriceValue : schematicMaterialsValue;
               HashPopUtils.drawTabularText(this.append, priceValue - priceValue5, priceValue2, 0.82F, hashOpenValue, hashOpenValue);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      }
   }

   @Override
   public String formatSchematicsText() {
      return this.cheapestLiveText;
   }

   private int getMaterialEstimated(String materialEstimatedText) {
      String upperCase = materialEstimatedText.toUpperCase(Locale.ROOT);
      if (!upperCase.equals("NAME") && !upperCase.equals("MATERIAL")) {
         for (int i = 0; i < this.materialEstimatedValue; i++) {
            if (this.estimatedMaterialTexts[i].equals(upperCase)) {
               return i;
            }
         }

         return this.materialsMissingValue;
      } else {
         return -1;
      }
   }

   private static float getCoveredMatches(SchematicMaterials.Line line) {
      if (!line.materialsCoveredActive) {
         return 1.0F;
      } else {
         float coveredMatchesValue = AmountPreviewUtils.getMin(Math.abs(line.materialsCoveredAmount - line.matchesCoveredAmount) / 20.0F * 2.5F);
         return 1.0F - 0.92F * getAmount(coveredMatchesValue);
      }
   }

   static {
      for (int i = 0; i <= 100; i++) {
         missingHotbarTexts[i] = i + "%";
      }
   }

   public void handleFilterEstimated(Result<Item> result) {
      if (this.estimatedFilterFlag && result != null) {
         this.handleCheapestLive(result, this.previewRail.isFlag());
      }
   }

   private static float getAmount(float schematicMaterialsFactor) {
      schematicMaterialsFactor = AmountPreviewUtils.getMin(schematicMaterialsFactor);
      return schematicMaterialsFactor * schematicMaterialsFactor * (3.0F - 2.0F * schematicMaterialsFactor);
   }

   private int getCompleteFilter(SchematicMaterials.Line line) {
      if (this.estimatedFilterFlag) {
         return CardMatrixUtils.getValue(CardMatrixUtils.getValue(-920587, -7630437, 0.45), -920587, line.highlightProgress);
      } else {
         return line.availableCompleteStatus == MaxMaterialsUtils.Status.COMPLETE ? -7630437 : -920587;
      }
   }

   private static void handleTextAmount(SchematicMaterials.Swap swap, float schematicMaterialsValue, float priceValue, float matrixStackFactor, int matrixStackValue, long schematicMaterialsValue5) {
      float schematicMaterialsLimit = swap.displayedSwapText == null ? 1.0F : (float)(schematicMaterialsValue5 - swap.swapValue) / 1000000.0F / 170.0F;
      if (schematicMaterialsLimit >= 1.0F) {
         swap.displayedSwapText = null;
         MatrixStackUtilsAmount.handlePushScale(swap.swapText, MatrixStackUtilsAmount.getAmount(schematicMaterialsValue - swap.getAmountText(matrixStackFactor)), priceValue, matrixStackFactor, matrixStackValue);
      } else {
         float min6 = AmountPreviewUtils.getMin(schematicMaterialsLimit / 0.55F);
         float panelOverlayLimit = AmountPreviewUtils.getAmount6((schematicMaterialsLimit - 0.25F) / 0.75F);
         if (min6 < 1.0F) {
            float schematicMaterialsValue7 = swap.cachedSwapWidth >= 0.0F ? swap.cachedSwapWidth : MatrixStackUtilsAmount.getAmount(swap.displayedSwapText, matrixStackFactor);
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(1.0F - min6);

            try {
               MatrixStackUtilsAmount.handlePushScale(swap.displayedSwapText, MatrixStackUtilsAmount.getAmount(schematicMaterialsValue - schematicMaterialsValue7), priceValue - 3.0F * min6, matrixStackFactor, matrixStackValue);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }

         if (panelOverlayLimit > 0.0F) {
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

            try {
               MatrixStackUtilsAmount.handlePushScale(
                  swap.swapText, MatrixStackUtilsAmount.getAmount(schematicMaterialsValue - swap.getAmountText(matrixStackFactor)), priceValue + 3.0F * (1.0F - panelOverlayLimit), matrixStackFactor, matrixStackValue
               );
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }
         }
      }
   }

   private void handleAmountCube(ButtonMin buttonMin) {
      if (buttonMin.cubeKnifeAmount != this.lastButtonX
         || buttonMin.cubeSchematicAmount != this.cachedPanelX
         || buttonMin.cubeSchematicAmount2Cube != this.lastButtonWidth
         || buttonMin.cubeSchematicAmount3Cube != this.cachedPanelWidth) {
         this.lastButtonX = buttonMin.cubeKnifeAmount;
         this.cachedPanelX = buttonMin.cubeSchematicAmount;
         this.lastButtonWidth = buttonMin.cubeSchematicAmount2Cube;
         this.cachedPanelWidth = buttonMin.cubeSchematicAmount3Cube;
         this.cubeAmount = buttonMin.cubeKnifeAmount + 12.0F;
         this.rowRightX = buttonMin.cubeKnifeAmount + buttonMin.cubeSchematicAmount2Cube - 12.0F;
         this.materialAmount = this.cubeAmount + 4.0F;
         this.listRightEdge = this.rowRightX - 4.0F;
         this.firstColumnX = MatrixStackUtilsAmount.getAmount(buttonMin.cubeSchematicAmount + 11.0F + 5.152F);
         this.secondColumnX = MatrixStackUtilsAmount.getAmount(this.firstColumnX + 6.0F + 12.88F);
         this.lastColumnX = MatrixStackUtilsAmount.getAmount(this.secondColumnX + 6.0F + 5.2808F);
         float cubeValue = this.lastColumnX + 4.0F;
         if (!this.estimatedFilterFlag) {
            this.headerLineY = MatrixStackUtilsAmount.getAmount(cubeValue + 3.0F);
            cubeValue = this.headerLineY + 2.0F;
         }

         this.searchBarY = MatrixStackUtilsAmount.getAmount(cubeValue + 10.0F);
         this.listTopY = MatrixStackUtilsAmount.getAmount(this.searchBarY + 14.0F + MatrixPeekUtilsRender.getAmount());
         float max2 = Math.max(0.0F, buttonMin.cubeSchematicAmount + buttonMin.cubeSchematicAmount3Cube - 4.0F - this.listTopY);
         float ticks = (float)Math.floor(max2 / 20.0F) * 20.0F;
         this.substringAmount = this.listTopY + (ticks >= 20.0F ? ticks : max2);
         this.priceColumnX = this.listRightEdge - Math.min(184.0F, (this.listRightEdge - this.materialAmount) * 0.46F);
         this.rowTextX = this.materialAmount + 16.0F + 7.0F;
         this.cachedCheapestBound = -1.0F;
      }
   }

   private void handleTypesNone(MaxMaterialsUtils.Plan<Item> plan, boolean typesNoneFlag) {
      long nanoTime = System.nanoTime();
      this.plan2TypesNone = plan;

      for (SchematicMaterials.Line line : this.materialsMatchItems) {
         line.typesNoneActive = false;
      }

      if (plan != null) {
         for (MaxMaterialsUtils.Row row : plan.readSelectedItems) {
            SchematicMaterials.Line materialsCost = this.getMaterialsCost((Item)row.next, typesNoneFlag);
            materialsCost.availableCompleteStatus = row.availableCompleteType;
            handleMaxTimes(materialsCost, 0, row.surveyedPlanValue, typesNoneFlag, nanoTime);
            handleMaxTimes(materialsCost, 1, row.minCount, typesNoneFlag, nanoTime);
            handleMaxTimes(materialsCost, 2, row.pricingMaterialValue, typesNoneFlag, nanoTime);
            handleMaxTimes(materialsCost, 3, row.typesNoneValue, typesNoneFlag, nanoTime);
            handleMaxTimes(materialsCost, 4, row.rowValue, typesNoneFlag, nanoTime);
            handleMaxTimes(materialsCost, 5, row.maxCount, typesNoneFlag, nanoTime);
         }
      }

      MaxMaterialsUtils.Summary summary = plan == null ? null : plan.typesTotal;
      double typesNoneValue = summary == null ? 0.0 : Math.max(0.0, Math.min(1.0, summary.fillRatio));
      long bound = (long)Math.floor(typesNoneValue * 10000.0);
      if (typesNoneFlag) {
         this.cheapestLiveBounds.canMax(bound, nanoTime, 480000000L);
      } else {
         this.cheapestLiveBounds.handleMax(bound);
      }

      int appendValue = summary == null ? 0 : summary.types;
      int typesNoneLimit = summary == null ? 0 : summary.missingTypes;
      this.typesNoneSwap
         .handleTextAmount(
            summary == null
               ? "No blocks yet"
               : BuyerAppendUtils.getAppendForLong(summary.blocksValue) + " of " + BuyerAppendUtils.getAppendForLong(summary.total) + " blocks placed",
            typesNoneFlag
         );
      this.cheapestLiveSwaps[0].handleTextAmount(summary == null ? "None" : BuyerAppendUtils.getAppendForLong(summary.remaining), typesNoneFlag);
      this.cheapestLiveSwaps[1].handleTextAmount(typesNoneLimit == 0 ? "None" : typesNoneLimit + (typesNoneLimit == 1 ? " type" : " types"), typesNoneFlag);
      this.cheapestLiveSwaps[2].handleTextAmount(BuyerAppendUtils.getAppendForLong(appendValue), typesNoneFlag);
      this.cheapestNoneValues[0] = -5854026;
      this.cheapestNoneValues[1] = typesNoneLimit > 0 ? typesMissingValue : -5854026;
      this.cheapestNoneValues[2] = -5854026;
      this.typesNoneText = null;
      this.cheapestLiveText = this.formatMaterialsTrim(appendValue);
      this.handleMatchMaterials(typesNoneFlag);
   }

   @Override
   public boolean canAmountMatrix(ButtonMin buttonMin, double matrixValue, double matrixValue2, double matrixValue3) {
      this.matrixCount += (float)matrixValue3;
      int matrixCount5 = (int)this.matrixCount;
      if (matrixCount5 != 0) {
         this.matrixCount -= matrixCount5;
         this.scrollAnimator.handleAmount(Math.round(this.scrollAnimator.getTargetScroll() / 20.0F - matrixCount5 * 2) * 20.0F);
      }

      return true;
   }

   private void handleAmountsAmount(int schematicMaterialsValue, String schematicMaterialsText, float schematicMaterialsValue2, float priceValue, float schematicMaterialsValue4, boolean schematicMaterialsFlag, int schematicMaterialsValue5, boolean materialFlag) {
      int hotOffset = schematicMaterialsValue + 1;
      if (materialFlag) {
         boolean schematicMaterialsFlag3 = schematicMaterialsValue5 == schematicMaterialsValue;
         this.schematicMaterialsTable[hotOffset] = AmountPreviewUtils.getAmount(this.schematicMaterialsTable[hotOffset], schematicMaterialsFlag3 ? 1.0F : 0.0F, schematicMaterialsFlag3 ? 16.0F : 11.0F, this.thumbsAmount, 0.002F);
      }

      float schematicMaterialsLimit = this.thumbsTable[hotOffset];
      float cardMatrixFactor = this.schematicMaterialsTable[hotOffset];
      int schematicMaterialsValue7 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-11117467, -7630437, cardMatrixFactor), -5854026, schematicMaterialsLimit);
      float schematicMaterialsValue8 = MatrixStackUtilsAmount.getAmount(schematicMaterialsText, 0.72F);
      float scaleValue = schematicMaterialsFlag ? MatrixStackUtilsAmount.getAmount(schematicMaterialsValue2 - schematicMaterialsValue8) : schematicMaterialsValue2;
      MatrixStackUtilsAmount.handlePushScale(schematicMaterialsText, scaleValue, priceValue, 0.72F, schematicMaterialsValue7);
      if (schematicMaterialsLimit > 0.01F) {
         float matrixStackValue = schematicMaterialsFlag ? scaleValue - 8.0F - 1.0F : scaleValue + schematicMaterialsValue8 + 1.0F;
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(schematicMaterialsLimit);

         try {
            MatrixStackUtilsRelease.handle(IcoGenUtils.getChevrondnIco(), matrixStackValue, MatrixStackUtilsAmount.getAmount(schematicMaterialsValue4 - 4.0F), 8.0F, schematicMaterialsValue7);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private void handleMatchMaterials(boolean matchMaterialsFlag) {
      for (SchematicMaterials.Line line : this.materialsMatchItems) {
         line.matchMaterialsActive = false;
      }

      if (!matchMaterialsFlag) {
         this.handleMaterialsMatch(true);
      }

      this.priceFlag = true;
      this.updateMaterialsCovered();
   }

   private void updateAmountsEstimated() {
      float listRightEdge15 = this.listRightEdge;

      for (int i = this.materialEstimatedValue - 1; i >= 0; i--) {
         this.priceAmounts[i] = listRightEdge15;
         listRightEdge15 -= this.estimatedTable[i] + 16.0F;
      }

      this.estimatedAmount = listRightEdge15 + 16.0F - 12.0F;
   }

   private float getTime(int schematicMaterialsBound, long schematicMaterialsValue) {
      float schematicMaterialsValue2 = (float)(schematicMaterialsValue - this.nanoTimeMaterial) / 1000000.0F - 150.0F - Math.min(Math.max(schematicMaterialsBound, 0), 12) * 18.0F;
      return AmountPreviewUtils.getAmount6(schematicMaterialsValue2 / 220.0F);
   }

   private String formatTextMax(SchematicMaterials.Line line) {
      float max = Math.max(0.0F, this.estimatedAmount - this.rowTextX);
      if (max != line.lastTextWidth) {
         line.lastTextWidth = max;
         line.lineText = MatrixStackUtilsAmount.getFontRenderer().formatText(line.cheapestLiveText, max / 0.9F);
      }

      return line.lineText;
   }

   private void handleSubstringLower(String substringLowerText) {
      String substring = substringLowerText == null ? "" : substringLowerText;
      if (substring.length() > 24) {
         substring = substring.substring(0, 24);
      }

      if (!substring.equals(this.searchText)) {
         this.searchText = substring;
         this.searchTextLower = substring.trim().toLowerCase(Locale.ROOT);
         this.search = "Search: " + substring + "  \u00d7";
         this.updateMaterialsCovered();
         this.scrollAnimator.handleAmount(0.0F);
      }
   }

   private void renderAmountMaterial(DrawContext context, long materialValue) {
      MatrixStackUtilsAmount.handlePushScale(this.placedEstimatedText, this.materialAmount + 1.0F, this.firstColumnX - 9.2F, 0.8F, -11117467);
      long time = this.cheapestLiveBounds.getMaxTime(materialValue);
      if (time != this.lastRenderNanos) {
         this.lastRenderNanos = time;
         if (this.estimatedFilterFlag) {
            this.materialText = BuyerAppendUtils.formatText(time);
            this.appendMaterial = BuyerAppendUtils.getAppendString(time);
         } else {
            this.materialText = missingHotbarTexts[(int)Math.max(0L, Math.min(100L, time / 100L))];
         }
      }

      float materialValue2 = this.secondColumnX - 23.0F;
      HashPopUtils.drawTabularText(this.materialText, this.materialAmount, materialValue2, 2.0F, -920587, this.estimatedFilterFlag ? ESTIMATED_FILTER_COLOR : -920587);
      HashPopUtils.drawTabularText(
         this.materialText, this.materialAmount + MatrixPeekUtilsRender.getAmount(), materialValue2, 2.0F, MATERIAL_DIM_COLOR, this.estimatedFilterFlag ? ESTIMATED_FILTER_DIM_COLOR : MATERIAL_DIM_COLOR
      );
      float priceValue = this.lastColumnX - 9.43F;
      if (this.estimatedFilterFlag) {
         long secondaryMaxBound = this.cheapestLiveBounds.getSecondaryMaxBound();
         if (secondaryMaxBound != this.materialValue) {
            this.materialValue = secondaryMaxBound;
            this.priceTextWidth = HashPopUtils.measureTextWidth(BuyerAppendUtils.getAppendString(secondaryMaxBound), 0.82F);
         }

         if (this.priceRevealProgress > 0.004F) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.priceRevealProgress);

            try {
               HashPopUtils.drawTextSingleColor(this.appendMaterial, this.materialAmount + 1.0F, priceValue, 0.82F, -7630437);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }

         this.handleTextPrice(this.materialAmount + 1.0F + (this.priceTextWidth + 7.0F) * this.priceRevealProgress, priceValue, materialValue);
      } else {
         handleTextTime(this.typesNoneSwap, this.materialAmount + 1.0F, priceValue, 0.82F, -7630437, materialValue);
         float materialFactor = this.listRightEdge - this.materialAmount;
         MatrixPeekUtilsRender.renderMatrixPeek(context, this.materialAmount, this.headerLineY, materialFactor, 2.0F, 1.0F, MatrixStackUtilsAmount.getValue(-1, 0.06F));
         float materialLimit = Math.max(0.0F, Math.min(1.0F, (float)time / 10000.0F));
         if (materialLimit > 5.0E-4F) {
            MatrixPeekUtilsRender.renderMatrixPeek(
               context,
               this.materialAmount,
               this.headerLineY,
               Math.max(2.0F, materialFactor * materialLimit),
               2.0F,
               1.0F,
               MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.9F)
            );
         }
      }

      if (this.estimatedFilterFlag && this.cheapestNoneText != null) {
         this.updateCheapestLive();
      }

      float millis = (float)(materialValue - this.nanoTimeMaterial) / 1000000.0F;
      float materialValue4 = MatrixStackUtilsAmount.getAmount((this.firstColumnX + this.lastColumnX) / 2.0F);

      for (int i = 0; i < 3; i++) {
         float panelOverlayLimit = AmountPreviewUtils.getAmount6((millis - 120.0F - i * 30.0F) / 200.0F);
         if (!(panelOverlayLimit <= 0.004F)) {
            float materialValue6 = i == 0 ? this.firstColumnX : (i == 1 ? materialValue4 : this.lastColumnX);
            float materialValue7 = materialValue6 - 9.43F;
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

            try {
               MatrixStackUtilsAmount.handlePushScale(this.materialsMissingTexts[i], this.priceColumnX, materialValue7, 0.82F, -11117467);
               handleTextAmount(this.cheapestLiveSwaps[i], this.listRightEdge, materialValue7, 0.82F, this.cheapestNoneValues[i], materialValue);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }
         }
      }
   }

   @Override
   public float getAmount() {
      return this.panelHeight;
   }

   private void updateMaterialsCovered() {
      this.materialsCostItems.clear();

      for (SchematicMaterials.Line line : this.materialsMatchItems) {
         line.materialsCoveredActive = line.typesNoneActive && this.canAvailableComplete(line);
         if (line.materialsCoveredActive) {
            this.materialsCostItems.add(line);
         }
      }

      this.materialsCostItems.sort(this.getThenComparing());

      for (int i = 0; i < this.materialsCostItems.size(); i++) {
         SchematicMaterials.Line materialscostitem = this.materialsCostItems.get(i);
         materialscostitem.matchesValue = i;
         materialscostitem.matchesCoveredAmount = i * 20.0F;
         if (Float.isNaN(materialscostitem.materialsCoveredAmount) || materialscostitem.matchesAmount <= 0.01F) {
            materialscostitem.materialsCoveredAmount = materialscostitem.matchesCoveredAmount;
         }
      }

      if (!this.searchTextLower.isEmpty()) {
         this.cube = "No matches";
         this.materialsCoveredText = "Nothing matches \"" + this.searchText + "\"";
      } else if (this.estimatedFilterFlag) {
         this.cube = "Nothing to buy";
         this.materialsCoveredText = "Every material is already covered";
      } else if (this.filter != SchematicMaterials.Filter.ALL && !this.materialsMatchItems.isEmpty()) {
         this.cube = "No materials match";
         this.materialsCoveredText = "Try another filter";
      } else {
         this.cube = "No materials";
         this.materialsCoveredText = "Load a schematic to see what it needs";
      }
   }

   private void renderCubeAmount(DrawContext context, ButtonMin buttonMin, long cubeValue, boolean cubeFlag) {
      float cubeLimit = Math.max(0.0F, this.substringAmount - this.listTopY);
      this.scrollAnimator.handleMaxAmount(this.materialsCostItems.size() * 20.0F, cubeLimit);
      float cubeValue2 = MatrixStackUtilsAmount.getAmount(this.scrollAnimator.getDisplayedScroll());
      boolean cubeFlag2 = buttonMin.thumbsActive
         && buttonMin.materialAmount >= this.cubeAmount
         && buttonMin.materialAmount <= this.rowRightX
         && buttonMin.cursorY >= this.listTopY
         && buttonMin.cursorY < this.substringAmount;
      int cubeLimit2 = this.materialsCostItems.size() - 1;
      float rootDivisor = MatrixPeekUtilsRender.getAmount();
      if (cubeLimit > 1.0F) {
         MatrixStackUtilsRelease.renderScissorEnable(context, buttonMin.cubeKnifeAmount, this.listTopY, buttonMin.cubeSchematicAmount2Cube, cubeLimit);

         try {
            int index = 0;

            for (int i = this.materialsMatchItems.size(); index < i; index++) {
               SchematicMaterials.Line materialsmatchitem = this.materialsMatchItems.get(index);
               float f = MatrixStackUtilsAmount.getAmount(this.listTopY + materialsmatchitem.materialsCoveredAmount - cubeValue2);
               boolean cubeFlag3 = materialsmatchitem.matchesAmount > 0.004F && f < this.substringAmount && f + 20.0F > this.listTopY;
               boolean cubeFlag4 = cubeFlag3 && cubeFlag2 && materialsmatchitem.materialsCoveredActive && buttonMin.cursorY >= f && buttonMin.cursorY < f + 20.0F;
               if (cubeFlag) {
                  materialsmatchitem.cubeAmount = AmountPreviewUtils.getAmount(materialsmatchitem.cubeAmount, cubeFlag4 ? 1.0F : 0.0F, cubeFlag4 ? 16.0F : 11.0F, this.thumbsAmount, 0.002F);
               }

               if (cubeFlag3) {
                  float cubeFactor = this.getTime(materialsmatchitem.matchesValue, cubeValue);
                  float panelOverlayLimit = materialsmatchitem.matchesAmount * cubeFactor * getCoveredMatches(materialsmatchitem) * this.scrollAnimator.getMax(f, f + 20.0F, this.listTopY, 8.0F);
                  if (!(panelOverlayLimit <= 0.004F)) {
                     this.renderAmountCube(context, materialsmatchitem, f + 4.0F * (1.0F - cubeFactor), panelOverlayLimit, materialsmatchitem.materialsCoveredActive && materialsmatchitem.matchesValue < cubeLimit2, rootDivisor, cubeValue);
                  }
               }
            }
         } finally {
            context.disableScissor();
         }

         MatrixStackUtilsRelease.render(context, buttonMin.cubeKnifeAmount + buttonMin.cubeSchematicAmount2Cube - 6.0F, this.listTopY + 2.0F, cubeLimit - 4.0F, this.scrollAnimator);
      }

      if (this.emptyStateAlpha > 0.01F) {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.emptyStateAlpha);

         try {
            MatrixStackUtilsRelease.render(
               context,
               this.cubeAmount,
               this.listTopY,
               this.rowRightX - this.cubeAmount,
               cubeLimit,
               MatrixStackUtilsRelease.getIdentifier("cube"),
               this.cube,
               this.materialsCoveredText
            );
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   public boolean isEstimatedFilterFlag() {
      return this.estimatedFilterFlag;
   }

   private static void accumulateMaterialCount(Map<Item, Integer> map, Map<Item, Long> map2, Item botCrystalText, int schematicMaterialsValue, long schematicMaterialsLimit) {
      map.put(botCrystalText, schematicMaterialsValue);
      if (schematicMaterialsLimit >= 0L) {
         map2.put(botCrystalText, schematicMaterialsLimit);
      }
   }

   private void handleCheapestLive(Result<Item> result, boolean cheapestLiveFlag) {
      long nanoTime = System.nanoTime();
      long cheapestLiveValue = this.cheapestLiveBounds.getSecondaryMaxBound();
      this.cheapestLiveResult = result;

      for (SchematicMaterials.Line line : this.materialsMatchItems) {
         line.typesNoneActive = false;
      }

      int materialsTrimValue = 0;
      int cheapestLiveLimit = 0;
      int buyCostValue3 = 0;
      long bound = 0L;
      if (result != null) {
         for (BuyerAppendUtils.Line line2 : result.cheapestLiveLineList) {
            SchematicMaterials.Line materialsCost = this.getMaterialsCost((Item)line2.itemKey, cheapestLiveFlag);
            materialsCost.cheapestLiveActive = line2.cheapestLiveActive;
            handleMaxTimes(materialsCost, 0, line2.maxQuantity, cheapestLiveFlag, nanoTime);
            handleMaxTimes(materialsCost, 1, line2.cheapestLiveActive ? line2.timeCost : 0L, cheapestLiveFlag, nanoTime);
            handleMaxTimes(materialsCost, 2, line2.cheapestLiveActive ? line2.cheapestLiveTime : 0L, cheapestLiveFlag, nanoTime);
            if (!cheapestLiveFlag || materialsCost.matchMaterialsActive) {
               materialsCost.highlightProgress = line2.cheapestLiveActive ? 1.0F : 0.0F;
            }
         }

         materialsTrimValue = result.getCostBuy();
         cheapestLiveLimit = result.costBuyValue;
         buyCostValue3 = result.buyCostValue;
         bound = Math.max(0L, result.costBuyTime);
      }

      if (cheapestLiveFlag) {
         if (this.cheapestLiveBounds.canMax(bound, nanoTime, 480000000L)) {
            this.append = BuyerAppendUtils.getAppend(bound - cheapestLiveValue);
            this.cheapestLiveActive = bound > cheapestLiveValue;
            this.nanoTimeCheapestLive = nanoTime;
         }
      } else {
         this.cheapestLiveBounds.handleMax(bound);
      }

      this.cheapestLiveSwaps[0].handleTextAmount(materialsTrimValue == 0 ? "None" : (cheapestLiveLimit == 0 ? "All " + materialsTrimValue : buyCostValue3 + " of " + materialsTrimValue), cheapestLiveFlag);
      this.cheapestLiveSwaps[1].handleTextAmount(cheapestLiveLimit == 0 ? "None" : BuyerAppendUtils.getAppendForLong(cheapestLiveLimit), cheapestLiveFlag);
      this.cheapestNoneValues[1] = cheapestLiveLimit > 0 ? missingPriceValue : -5854026;
      var line3 = result == null ? null : result.getNext();
      SchematicMaterials.Line line4 = line3 == null ? null : this.costMaterialsMap.get(line3.itemKey);
      this.cheapestNoneText = line4 == null ? null : line4.cheapestLiveText;
      this.cheapestLiveTime = line3 == null ? -1L : line3.cheapestLiveTime;
      this.cachedCheapestBound = -1.0F;
      if (this.cheapestNoneText == null) {
         this.cheapestLiveSwaps[2].handleTextAmount("None", cheapestLiveFlag);
      }

      this.cheapestNoneValues[2] = -5854026;
      this.typesNoneText = cheapestLiveLimit > 0 ? "Cheapest live listing per item \u00b7 unpriced items are not in the total" : "Cheapest live listing per item";
      this.cheapestLiveText = this.formatMaterialsTrim(materialsTrimValue);
      this.handleMatchMaterials(cheapestLiveFlag);
   }

   private Comparator<SchematicMaterials.Line> getThenComparing() {
      Comparator<SchematicMaterials.Line> comparing = Comparator.<SchematicMaterials.Line, String>comparing(line -> line.lowerCase);
      if (this.materialsMissingValue == -1) {
         return comparing;
      } else {
         int materialsMissingValue = this.materialsMissingValue;
         Comparator reversed = Comparator.<SchematicMaterials.Line>comparingLong(line2 -> line2.availableCompleteTable[materialsMissingValue]).reversed();
         return this.estimatedFilterFlag && materialsMissingValue != 0
            ? Comparator.<SchematicMaterials.Line, Boolean>comparing(line3 -> !line3.cheapestLiveActive).thenComparing(reversed).thenComparing(comparing)
            : reversed.thenComparing(comparing);
      }
   }

   private static Result<Item> getObsidianBlock() {
      var linkedHashMap = new LinkedHashMap();
      var linkedHashMap2 = new LinkedHashMap();
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.NETHERITE_BLOCK, 2304, 1450000L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.BEACON, 64, 2900000L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.DIAMOND_BLOCK, 1152, 41250L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.OBSIDIAN, 12800, 520L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.GOLD_BLOCK, 640, 9800L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.CRYING_OBSIDIAN, 900, -1L);
      return BuyerAppendUtils.getClassForMap(linkedHashMap, linkedHashMap2);
   }

   private static void handleMaxTimes(SchematicMaterials.Line line, int index, long schematicMaterialsValue, boolean schematicMaterialsFlag, long longValue) {
      line.availableCompleteTable[index] = schematicMaterialsValue;
      if (schematicMaterialsFlag && !line.matchMaterialsActive) {
         line.lineMax16s[index].canMax(schematicMaterialsValue, longValue, 480000000L);
      } else {
         line.lineMax16s[index].handleMax(schematicMaterialsValue);
      }
   }

   @Override
   public boolean canMaxSubstring(int substringValue, int substringValue2) {
      switch (substringValue) {
         case 259:
            if (this.searchText.isEmpty()) {
               return false;
            }

            this.handleSubstringLower((substringValue2 & 2) != 0 ? "" : this.searchText.substring(0, this.searchText.length() - 1));
            return true;
         case 260:
         case 262:
         case 263:
         default:
            return false;
         case 261:
            if (this.searchText.isEmpty()) {
               return false;
            }

            this.handleSubstringLower("");
            return true;
         case 264:
            this.scrollAnimator.handleAmount(this.scrollAnimator.getTargetScroll() + 20.0F);
            break;
         case 265:
            this.scrollAnimator.handleAmount(this.scrollAnimator.getTargetScroll() - 20.0F);
            break;
         case 266:
            this.scrollAnimator.handleAmount(this.scrollAnimator.getTargetScroll() - Math.max(20.0F, this.substringAmount - this.listTopY - 20.0F));
            break;
         case 267:
            this.scrollAnimator.handleAmount(this.scrollAnimator.getTargetScroll() + Math.max(20.0F, this.substringAmount - this.listTopY - 20.0F));
            break;
         case 268:
            this.scrollAnimator.handleAmount(0.0F);
            break;
         case 269:
            this.scrollAnimator.handleAmount(this.scrollAnimator.getMax());
      }

      return true;
   }

   private SchematicMaterials.Line getMaterialsCost(Item botCrystalText, boolean materialsCostFlag) {
      SchematicMaterials.Line costmaterialsmap = this.costMaterialsMap.get(botCrystalText);
      if (costmaterialsmap == null) {
         costmaterialsmap = new SchematicMaterials.Line(botCrystalText);
         costmaterialsmap.matchMaterialsActive = true;
         costmaterialsmap.matchesAmount = materialsCostFlag ? 0.0F : 1.0F;
         this.materialsMatchItems.add(costmaterialsmap);
         this.costMaterialsMap.put(botCrystalText, costmaterialsmap);
      }

      costmaterialsmap.typesNoneActive = true;
      return costmaterialsmap;
   }

   private String formatTextsTimes(SchematicMaterials.Line line, int index, long schematicMaterialsValue) {
      long maxTime = line.lineMax16s[index].getMaxTime(schematicMaterialsValue);
      if (maxTime != line.lineTable[index] || line.lineTexts[index] == null) {
         line.lineTable[index] = maxTime;
         line.lineTexts[index] = this.formatFilterEstimated(index, maxTime);
      }

      return line.lineTexts[index];
   }

   public SchematicMaterials(Screen screen, MaxMaterialsUtils.Plan<Item> plan, String schematicMaterialsText) {
      this(screen, plan, schematicMaterialsText, null);
   }

   @Override
   public float getSchematicMaterialsAmount() {
      return this.schematicMaterialsAmount;
   }

   @Override
   public void handleFolderDone(PreviewText previewText) {
      if (this.estimatedFilterFlag) {
         previewText.getText(this.typesNoneText);
      } else if (this.searchText.isEmpty()) {
         previewText.getText("Type to search");
      }

      if (!this.estimatedFilterFlag) {
         previewText.getClassForStringIntConsumer("filter", "Show", this.filter.filterSearchText, this.intConsumer);
      }

      if (!this.searchText.isEmpty()) {
         previewText.getClassForStringRunnable("search", this.search, this.doneSearchAction);
      }

      previewText.getFlag().getClass073("done", "Done", this.done);
   }

   private static MaxMaterialsUtils.Plan<Item> getLanternCyan(boolean lanternCyanFlag) {
      var linkedHashMap = new LinkedHashMap();
      var linkedHashMap2 = new LinkedHashMap();
      var linkedHashMap3 = new LinkedHashMap();
      var hashMap = new LinkedHashMap();
      accumulateMaterialTotals(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap, Items.OBSIDIAN, 1280, lanternCyanFlag ? 900 : 640, 64, 128);
      accumulateMaterialTotals(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap, Items.WHITE_CONCRETE, 4096, lanternCyanFlag ? 3000 : 2400, 64, 512);
      accumulateMaterialTotals(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap, Items.SEA_LANTERN, 96, 96, 0, 0);
      accumulateMaterialTotals(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap, Items.QUARTZ_BLOCK, 640, 120, 0, 0);
      accumulateMaterialTotals(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap, Items.STONE_BRICKS, 3200, lanternCyanFlag ? 2900 : 2100, 128, 1300);
      accumulateMaterialTotals(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap, Items.GLASS, 960, 300, 64, 64);
      accumulateMaterialTotals(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap, Items.SPRUCE_PLANKS, 2304, 2304, 0, 0);
      accumulateMaterialTotals(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap, Items.LANTERN, 64, 10, 0, 12);
      accumulateMaterialTotals(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap, Items.CYAN_TERRACOTTA, 800, 0, 0, 0);
      accumulateMaterialTotals(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap, Items.REDSTONE_LAMP, 48, 0, 16, 0);
      return MaxMaterialsUtils.getClassForMap(linkedHashMap, linkedHashMap2, linkedHashMap3, hashMap);
   }

   private void renderPriceAmounts(DrawContext context, SchematicMaterials.Line line, float priceValue, float rootDivisor, long priceValue3) {
      HashPopUtils.drawTextRightAligned(this.formatTextsTimes(line, 0, priceValue3), this.priceAmounts[0], priceValue, 0.9F, -7630437);
      float highlightProgress = line.highlightProgress;
      float priceLimit = getAmount((highlightProgress - 0.45F) / 0.55F);
      float panelOverlayLimit = 1.0F - getAmount(highlightProgress / 0.55F);
      if (priceLimit > 0.004F) {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(priceLimit);

         try {
            HashPopUtils.drawTextRightAligned(this.formatTextsTimes(line, 1, priceValue3), this.priceAmounts[1], priceValue, 0.9F, -7630437);
            HashPopUtils.drawTextRightAligned(this.formatTextsTimes(line, 2, priceValue3), this.priceAmounts[2], priceValue, 0.9F, -920587);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }

      if (panelOverlayLimit > 0.004F) {
         float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            float ctxOffset = priceValue + 7.4519997F;
            MatrixPeekUtilsRender.renderMatrixPeek(context, this.priceAmounts[1] - 5.0F, MatrixStackUtilsAmount.getAmount(ctxOffset - rootDivisor), 5.0F, rootDivisor * 2.0F, -11117467);
            MatrixStackUtilsAmount.handlePushScale(
               "No price", MatrixStackUtilsAmount.getAmount(this.priceAmounts[2] - this.priceAmount), priceValue, 0.9F, missingPriceValue
            );
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
         }
      }
   }

   private static Result<Item> getLanternSpruce(boolean lanternSpruceFlag) {
      var linkedHashMap = new LinkedHashMap();
      var linkedHashMap2 = new LinkedHashMap();
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.OBSIDIAN, 1280, lanternSpruceFlag ? 610L : 520L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.WHITE_CONCRETE, 4096, lanternSpruceFlag ? 41L : 38L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.SEA_LANTERN, 96, lanternSpruceFlag ? 1320L : 1450L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.QUARTZ_BLOCK, 640, 85L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.STONE_BRICKS, lanternSpruceFlag ? 3600 : 3200, lanternSpruceFlag ? 11L : 9L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.GLASS, 960, 12L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.SPRUCE_PLANKS, 2304, 4L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.LANTERN, 64, 160L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.CYAN_TERRACOTTA, 800, 22L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.REDSTONE_LAMP, 48, lanternSpruceFlag ? 240L : -1L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.BEACON, 2, -1L);
      if (lanternSpruceFlag) {
         accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.IRON_BARS, 120, 30L);
      } else {
         accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.SPRUCE_STAIRS, 300, 14L);
      }

      return BuyerAppendUtils.getClassForMap(linkedHashMap, linkedHashMap2);
   }

   @Override
   public boolean canMatrixStack(ButtonMin buttonMin, double matrixStackValue, double matrixStackLimit, int matrixStackValue2) {
      if (matrixStackValue2 == 0 && !(matrixStackLimit < this.searchBarY) && !(matrixStackLimit > this.searchBarY + 14.0F + 2.0F)) {
         int amountsPrice = this.getAmountsPrice(matrixStackValue);
         if (amountsPrice != Integer.MIN_VALUE) {
            this.handleMaterialsMissing(amountsPrice);
         }

         return true;
      } else {
         return true;
      }
   }

   private void updatePriceEstimated() {
      this.priceFlag = false;
      boolean priceEstimatedFlag = false;
      this.priceAmount = MatrixStackUtilsAmount.getAmount("No price", 0.9F);

      for (int i = 0; i < this.materialEstimatedValue; i++) {
         float priceEstimatedBound = MatrixStackUtilsAmount.getAmount(this.estimatedMaterialTexts[i], 0.72F);

         for (SchematicMaterials.Line line : this.materialsMatchItems) {
            if (line.typesNoneActive) {
               if (this.estimatedFilterFlag && i != 0 && !line.cheapestLiveActive) {
                  priceEstimatedFlag = true;
               } else {
                  priceEstimatedBound = Math.max(priceEstimatedBound, HashPopUtils.measureTextWidth(this.formatFilterEstimated(i, line.availableCompleteTable[i]), 0.9F));
               }
            }
         }

         if (this.estimatedFilterFlag && i == 2 && priceEstimatedFlag) {
            priceEstimatedBound = Math.max(priceEstimatedBound, this.priceAmount);
         }

         this.priceEstimatedTable[i] = (float)Math.ceil(priceEstimatedBound);
      }

      if (!this.priceEstimatedActive) {
         this.priceEstimatedActive = true;
         System.arraycopy(this.priceEstimatedTable, 0, this.estimatedTable, 0, 6);
      }
   }

   private void handleMaterialsMissing(int materialsMissingValue) {
      if (materialsMissingValue != this.materialsMissingValue && materialsMissingValue >= -1 && materialsMissingValue < this.materialEstimatedValue) {
         this.materialsMissingValue = materialsMissingValue;
         this.updateMaterialsCovered();
      }
   }

   private static void handleTextTime(SchematicMaterials.Swap swap, float schematicMaterialsValue, float priceValue, float x2, int matrixStackValue, long schematicMaterialsValue5) {
      float schematicMaterialsLimit = swap.displayedSwapText == null ? 1.0F : (float)(schematicMaterialsValue5 - swap.swapValue) / 1000000.0F / 170.0F;
      if (schematicMaterialsLimit >= 1.0F) {
         swap.displayedSwapText = null;
         MatrixStackUtilsAmount.handlePushScale(swap.swapText, schematicMaterialsValue, priceValue, x2, matrixStackValue);
      } else {
         float min6 = AmountPreviewUtils.getMin(schematicMaterialsLimit / 0.55F);
         float panelOverlayLimit = AmountPreviewUtils.getAmount6((schematicMaterialsLimit - 0.25F) / 0.75F);
         if (min6 < 1.0F) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(1.0F - min6);

            try {
               MatrixStackUtilsAmount.handlePushScale(swap.displayedSwapText, schematicMaterialsValue, priceValue - 3.0F * min6, x2, matrixStackValue);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }

         if (panelOverlayLimit > 0.0F) {
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

            try {
               MatrixStackUtilsAmount.handlePushScale(swap.swapText, schematicMaterialsValue, priceValue + 3.0F * (1.0F - panelOverlayLimit), x2, matrixStackValue);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }
         }
      }
   }

   private String formatFilterEstimated(int filterEstimatedValue, long buyerAppendValue) {
      return this.estimatedFilterFlag && filterEstimatedValue != 0 ? BuyerAppendUtils.formatText(buyerAppendValue) : BuyerAppendUtils.getAppendForLong(buyerAppendValue);
   }

   @Override
   public boolean canMatrixStack(int matrixStackLimit, int matrixStackValue) {
      if (matrixStackLimit < 32 || matrixStackLimit > 126) {
         return false;
      } else if (this.searchText.isEmpty() && matrixStackLimit == 32) {
         return true;
      } else {
         this.handleSubstringLower(this.searchText + (char)matrixStackLimit);
         return true;
      }
   }

   private void renderMaterialAmount(DrawContext context, ButtonMin buttonMin, boolean materialFlag) {
      float materialValue = MatrixStackUtilsAmount.getAmount(this.searchBarY, 14.0F, 0.72F);
      float materialValue2 = materialValue + 5.9616F;
      boolean materialFlag2 = buttonMin.thumbsActive && buttonMin.cursorY >= this.searchBarY && buttonMin.cursorY <= this.searchBarY + 14.0F + 2.0F;
      int materialValue3 = materialFlag2 ? this.getAmountsPrice(buttonMin.materialAmount) : Integer.MIN_VALUE;
      this.handleAmountsAmount(-1, "MATERIAL", this.materialAmount, materialValue, materialValue2, false, materialValue3, materialFlag);

      for (int i = 0; i < this.materialEstimatedValue; i++) {
         this.handleAmountsAmount(i, this.estimatedMaterialTexts[i], this.priceAmounts[i], materialValue, materialValue2, true, materialValue3, materialFlag);
      }

      MatrixPeekUtilsRender.renderMatrixPeek(
         context,
         this.cubeAmount,
         MatrixStackUtilsAmount.getAmount(this.searchBarY + 14.0F),
         this.rowRightX - this.cubeAmount,
         MatrixPeekUtilsRender.getAmount(),
         CardMatrixUtils.getValueInt()
      );
   }

   private void updateCheapestLive() {
      if (!(this.cachedCheapestBound >= 0.0F) || this.cheapestNoneText != this.schematicMaterialsText || this.cheapestLiveTime != this.cheapestLiveValue) {
         float cheapestLiveValue = MatrixStackUtilsAmount.getAmount(this.materialsMissingTexts[2], 0.82F);
         float cheapestLiveBound = this.listRightEdge - this.priceColumnX - cheapestLiveValue - 10.0F;
         this.cachedCheapestBound = Math.max(0.0F, cheapestLiveBound);
         this.schematicMaterialsText = this.cheapestNoneText;
         this.cheapestLiveValue = this.cheapestLiveTime;
         String cheapestLiveText = " \u00b7 " + BuyerAppendUtils.formatText(this.cheapestLiveTime);
         float vertexTextureLimit = Math.max(0.0F, cheapestLiveBound / 0.82F - MatrixStackUtilsAmount.getAmount3(cheapestLiveText));
         String formatText = MatrixStackUtilsAmount.getFontRenderer().formatText(this.cheapestNoneText, vertexTextureLimit);
         this.cheapestLiveSwaps[2].handleTextAmount(formatText + cheapestLiveText, this.previewRail.isFlag());
      }
   }

   private void refreshDerivedState() {
      this.handleSubstringLower("");
   }

   private void handleMaterialsMatch(boolean materialsMatchFlag) {
      for (int i = this.materialsMatchItems.size() - 1; i >= 0; i--) {
         SchematicMaterials.Line materialsmatchitem = this.materialsMatchItems.get(i);
         if (!materialsmatchitem.typesNoneActive && (materialsMatchFlag || materialsmatchitem.matchesAmount <= 0.0F)) {
            this.materialsMatchItems.remove(i);
            this.costMaterialsMap.remove(materialsmatchitem.materialItem);
         }
      }
   }

   private void handleFilterMax(int filterValue) {
      SchematicMaterials.Filter[] filters = SchematicMaterials.Filter.values();
      this.filter = filters[Math.floorMod(this.filter.ordinal() + (filterValue < 0 ? -1 : 1), filters.length)];
      this.updateMaterialsCovered();
      this.scrollAnimator.handleAmount(0.0F);
   }

   private boolean canAvailableComplete(SchematicMaterials.Line line) {
      if (!this.searchTextLower.isEmpty() && !line.lowerCase.contains(this.searchTextLower) && !line.path.contains(this.searchTextLower)) {
         return false;
      } else if (this.estimatedFilterFlag) {
         return true;
      } else {
         return switch (this.filter) {
            case ALL -> true;
            case MISSING -> line.availableCompleteStatus == MaxMaterialsUtils.Status.MISSING || line.availableCompleteTable[5] > 0L;
            case LOW -> line.availableCompleteStatus == MaxMaterialsUtils.Status.LOW;
            case AVAILABLE -> line.availableCompleteStatus == MaxMaterialsUtils.Status.AVAILABLE;
            case COMPLETE -> line.availableCompleteStatus == MaxMaterialsUtils.Status.COMPLETE;
            case HOTBAR -> line.availableCompleteTable[3] > 0L;
            case INVENTORY -> line.availableCompleteTable[4] > 0L;
         };
      }
   }

   private static void renderMatrixPop(DrawContext context, ItemStack itemStack, float matrixPopValue, float hashRegistryValue) {
      int matricesMatrixValue = (int)Math.floor(matrixPopValue);
      int floorCtx = (int)Math.floor(hashRegistryValue);
      float matrixPopLimit = matrixPopValue - matricesMatrixValue;
      float matrixPopLimit2 = hashRegistryValue - floorCtx;
      boolean matrixPopFlag = matrixPopLimit > 0.001F || matrixPopLimit2 > 0.001F;
      Matrix3x2fStack matrices = context.getMatrices();
      if (matrixPopFlag) {
         matrices.pushMatrix();
         matrices.translate(matrixPopLimit, matrixPopLimit2);
      }

      try {
         DrawMatricesUtils.renderMatricesMatrix(context, itemStack, matricesMatrixValue, floorCtx, 16);
      } finally {
         if (matrixPopFlag) {
            matrices.popMatrix();
         }
      }
   }

   private String formatMaterialsTrim(int materialsTrimValue) {
      String materialsTrimText = materialsTrimValue + (materialsTrimValue == 1 ? " material" : " materials");
      return this.materialsTrimText == null ? materialsTrimText : this.materialsTrimText + "  \u00b7  " + materialsTrimText;
   }

   private int getAmountsPrice(double priceLimit) {
      if (priceLimit >= this.cubeAmount && priceLimit <= this.estimatedAmount) {
         return -1;
      } else {
         for (int i = 0; i < this.materialEstimatedValue; i++) {
            if (priceLimit >= this.priceAmounts[i] - this.estimatedTable[i] - 8.0F - 2.0F && priceLimit <= this.priceAmounts[i] + 4.0F) {
               return i;
            }
         }

         return Integer.MIN_VALUE;
      }
   }

   private void handleEstimatedTypes(SchematicMaterials.Line line, float estimatedTypesValue, long estimatedTypesValue2) {
      for (int i = 0; i < this.materialEstimatedValue; i++) {
         boolean estimatedTypesFlag = line.availableCompleteTable[i] > 0L;
         int hashOpenValue;
         if (!estimatedTypesFlag) {
            hashOpenValue = -11117467;
         } else if (i == 2) {
            hashOpenValue = -920587;
         } else if (i == 5) {
            hashOpenValue = typesMissingValue;
         } else {
            hashOpenValue = -7630437;
         }

         HashPopUtils.drawTextRightAligned(this.formatTextsTimes(line, i, estimatedTypesValue2), this.priceAmounts[i], estimatedTypesValue, 0.9F, hashOpenValue);
      }
   }

   private static void accumulateMaterialTotals(
      Map<Item, Integer> map, Map<Item, Integer> map2, Map<Item, Integer> map3, Map<Item, Integer> hashMap, Item botCrystalText, int schematicMaterialsValue, int schematicMaterialsValue2, int schematicMaterialsValue3, int botCrystalValue
   ) {
      map.put(botCrystalText, schematicMaterialsValue);
      map2.put(botCrystalText, schematicMaterialsValue2);
      map3.put(botCrystalText, schematicMaterialsValue3);
      hashMap.put(botCrystalText, botCrystalValue);
   }

   private static Result<Item> getGlassObsidian() {
      var linkedHashMap = new LinkedHashMap();
      var linkedHashMap2 = new LinkedHashMap();
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.OBSIDIAN, 64, 520L);
      accumulateMaterialCount(linkedHashMap, linkedHashMap2, Items.GLASS, 32, 12L);
      return BuyerAppendUtils.getClassForMap(linkedHashMap, linkedHashMap2);
   }

   private void renderAmountCube(DrawContext context, SchematicMaterials.Line line, float cubeValue, float panelOverlayLimit, boolean cubeFlag, float rootDivisor, long cubeValue4) {
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

      try {
         if (line.cubeAmount > 0.01F) {
            MatrixPeekUtilsRender.renderMatrixPeek(
               context, this.cubeAmount, cubeValue + 1.0F, this.rowRightX - this.cubeAmount, 18.0F, 5.0F, MatrixStackUtilsAmount.getValue(-1, 0.045F * line.cubeAmount)
            );
         }

         if (cubeFlag) {
            MatrixPeekUtilsRender.renderMatrixPeek(context, this.materialAmount, cubeValue + 20.0F - rootDivisor, this.listRightEdge - this.materialAmount, rootDivisor, 234881023);
         }

         renderMatrixPop(context, line.itemStack, this.materialAmount, cubeValue + 2.0F);
         float priceValue = MatrixStackUtilsAmount.getAmount(cubeValue, 20.0F, 0.9F);
         MatrixStackUtilsAmount.handlePushScale(this.formatTextMax(line), this.rowTextX, priceValue, 0.9F, this.getCompleteFilter(line));
         if (this.estimatedFilterFlag) {
            this.renderPriceAmounts(context, line, priceValue, rootDivisor, cubeValue4);
         } else {
            this.handleEstimatedTypes(line, priceValue, cubeValue4);
         }
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   @Override
   protected void updateTimeMax() {
      this.nanoTimeMaterial = System.nanoTime();
      this.cheapestLiveBounds.handleMaxTime(0L, this.cheapestLiveBounds.getSecondaryMaxBound(), this.nanoTimeMaterial + 160000000L, 900000000L);
   }

   @Override
   public void handleThumbsAmount(float amount) {
      this.thumbsCount++;
      this.thumbsAmount = amount;
      boolean thumbsFlag = false;

      for (SchematicMaterials.Line line : this.materialsMatchItems) {
         line.materialsCoveredAmount = AmountPreviewUtils.getAmount(line.materialsCoveredAmount, line.matchesCoveredAmount, 16.0F, amount, 0.01F);
         float thumbsLimit = line.materialsCoveredActive ? 1.0F : 0.0F;
         line.matchesAmount = AmountPreviewUtils.getAmount(line.matchesAmount, thumbsLimit, thumbsLimit > line.matchesAmount ? 12.0F : 26.0F, amount, 0.004F);
         line.highlightProgress = AmountPreviewUtils.getAmount(line.highlightProgress, line.cheapestLiveActive ? 1.0F : 0.0F, 10.0F, amount, 0.004F);
         if (!line.typesNoneActive && line.matchesAmount <= 0.0F) {
            thumbsFlag = true;
         }
      }

      if (thumbsFlag) {
         this.handleMaterialsMatch(false);
      }

      for (int i = 0; i < this.materialEstimatedValue; i++) {
         this.estimatedTable[i] = AmountPreviewUtils.getAmount(this.estimatedTable[i], this.priceEstimatedTable[i], 14.0F, amount, 0.02F);
      }

      for (int j = 0; j <= this.materialEstimatedValue; j++) {
         this.thumbsTable[j] = AmountPreviewUtils.getAmount(this.thumbsTable[j], this.materialsMissingValue + 1 == j ? 1.0F : 0.0F, 14.0F, amount, 0.004F);
      }

      this.priceRevealProgress = AmountPreviewUtils.getAmount(
         this.priceRevealProgress, this.estimatedFilterFlag && BuyerAppendUtils.canFlag(this.cheapestLiveBounds.getSecondaryMaxBound()) ? 1.0F : 0.0F, 10.0F, amount, 0.004F
      );
      this.emptyStateAlpha = AmountPreviewUtils.getAmount(this.emptyStateAlpha, this.materialsCostItems.isEmpty() ? 1.0F : 0.0F, 10.0F, amount, 0.004F);
      this.scrollAnimator.handleAmountMax(amount, this.previewRail.getButtonMin().thumbsActive);
   }

   public static void handleHarbourLighthouse(String harbourLighthouseText) {
      MinecraftClient instance = MinecraftClient.getInstance();
      String harbourLighthouseText2 = harbourLighthouseText == null ? "" : harbourLighthouseText.trim();
      String lowerCase = harbourLighthouseText2.toLowerCase(Locale.ROOT);
      SchematicMaterials schematicMaterials = instance.currentScreen instanceof SchematicMaterials schematicMaterials2 ? schematicMaterials2 : null;
      if (lowerCase.equals("change")) {
         if (schematicMaterials == null) {
            return;
         }

         harbourLighthouseFlag = !harbourLighthouseFlag;
         if (schematicMaterials.estimatedFilterFlag) {
            schematicMaterials.handleFilterEstimated(getLanternSpruce(harbourLighthouseFlag));
         } else {
            schematicMaterials.handleFilterEstimated(getLanternCyan(harbourLighthouseFlag));
         }
      } else if (lowerCase.equals("materials")) {
         harbourLighthouseFlag = false;
         instance.setScreen(new SchematicMaterials(instance.currentScreen, getLanternCyan(false), "Harbour Lighthouse"));
      } else if (lowerCase.startsWith("sort ")) {
         if (schematicMaterials != null) {
            schematicMaterials.handleMaterialsMissing(schematicMaterials.getMaterialEstimated(harbourLighthouseText2.substring(5).trim()));
         }
      } else if (lowerCase.startsWith("search")) {
         if (schematicMaterials != null) {
            schematicMaterials.handleSubstringLower(harbourLighthouseText2.length() > 6 ? harbourLighthouseText2.substring(6).trim() : "");
         }
      } else if (lowerCase.equals("filter")) {
         if (schematicMaterials != null && !schematicMaterials.estimatedFilterFlag) {
            schematicMaterials.handleFilterMax(1);
         }
      } else if (!lowerCase.equals("big") && !lowerCase.equals("tiny")) {
         harbourLighthouseFlag = false;
         instance.setScreen(new SchematicMaterials(instance.currentScreen, getLanternCyan(false), "Harbour Lighthouse", getLanternSpruce(false)));
      } else {
         harbourLighthouseFlag = false;
         boolean equals = lowerCase.equals("big");
         instance.setScreen(
            new SchematicMaterials(
               instance.currentScreen, getLanternCyan(false), equals ? "Netherite Citadel" : "Starter Hut", equals ? getObsidianBlock() : getGlassObsidian()
            )
         );
      }
   }

   private static enum Filter {
      ALL("All materials"),
      MISSING("Missing"),
      LOW("Running low"),
      AVAILABLE("Available"),
      COMPLETE("Complete"),
      HOTBAR("In hotbar"),
      INVENTORY("In inventory");
      final String filterSearchText;

      private Filter(String filterSearchText) {
         this.filterSearchText = filterSearchText;
      }
   }

   private static final class Line {
      final long[] lineTable;
      boolean materialsCoveredActive;
      final String[] lineTexts;
      final Item materialItem;
      final long[] availableCompleteTable = new long[6];
      float lastTextWidth;
      float matchesAmount;
      boolean matchMaterialsActive;
      int matchesValue;
      float matchesCoveredAmount;
      final String cheapestLiveText;
      final String path;
      final String lowerCase;
      float highlightProgress;
      MaxMaterialsUtils.Status availableCompleteStatus;
      boolean cheapestLiveActive;
      final ItemStack itemStack;
      boolean typesNoneActive;
      float materialsCoveredAmount;
      float cubeAmount;
      final MaxRamp[] lineMax16s;
      String lineText;

      Line(Item item2) {
         this.lineTable = new long[6];
         this.lineTexts = new String[6];
         this.lineMax16s = new MaxRamp[6];
         this.availableCompleteStatus = MaxMaterialsUtils.Status.AVAILABLE;
         this.cheapestLiveActive = true;
         this.typesNoneActive = true;
         this.materialsCoveredActive = true;
         this.highlightProgress = 1.0F;
         this.materialsCoveredAmount = Float.NaN;
         this.matchesAmount = 1.0F;
         this.lastTextWidth = -1.0F;
         this.lineText = "";
         this.materialItem = item2;
         this.itemStack = new ItemStack(item2);
         this.cheapestLiveText = this.itemStack.getName().getString();
         this.lowerCase = this.cheapestLiveText.toLowerCase(Locale.ROOT);
         this.path = Registries.ITEM.getId(item2).getPath();

         for (int i = 0; i < 6; i++) {
            this.lineMax16s[i] = new MaxRamp(0L);
            this.lineTable[i] = Long.MIN_VALUE;
         }
      }
   }

   private static final class Swap {
      float cachedSwapWidth;
      String swapText = "";
      float swapAmount = -1.0F;
      float cachedAmountKey = 1.0F;
      String displayedSwapText;
      long swapValue;

      void handleTextAmount(String swapText, boolean swapFlag) {
         String text = swapText == null ? "" : swapText;
         if (!text.equals(this.swapText)) {
            this.displayedSwapText = swapFlag && !this.swapText.isEmpty() ? this.swapText : null;
            this.cachedSwapWidth = this.swapAmount;
            this.swapText = text;
            this.swapAmount = -1.0F;
            this.swapValue = System.nanoTime();
         }
      }

      float getAmountText(float amount) {
         if (this.swapAmount < 0.0F || amount != this.cachedAmountKey) {
            this.cachedAmountKey = amount;
            this.swapAmount = MatrixStackUtilsAmount.getAmount(this.swapText, amount);
         }

         return this.swapAmount;
      }
   }
}
