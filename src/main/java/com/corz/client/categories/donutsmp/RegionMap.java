package com.corz.client.categories.donutsmp;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.NativeHud;
import com.corz.client.gui.TickProgress;
import com.corz.client.gui.VertexGuiUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.VertexTextureFont;
import com.corz.client.render.VertexTexturePeek;
import com.corz.client.categories.visuals.ShowHUD.HudScale;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import com.corz.client.categories.misc.FakeCoordinates;
import com.corz.client.utils.HudFontUtils;
import com.corz.client.categories.visuals.ShowHUD;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class RegionMap extends Module {
   private final BooleanSetting showCellNumbers;
   private final float[] cellNumberXs;
   private static final Color OFF_WHITE = new Color(15987704, false);
   private final BooleanSetting legend;
   private final float[] legendLabelYs;
   private final int[] legendLabelColors;
   private final float[] legendLabelXs;
   private static final String[] REGION_ID_TEXTS = new String[81];
   private static final int[][] REGION_TABLE_ROWS = new int[][]{
      {82, 5},
      {100, 3},
      {101, 3},
      {102, 3},
      {103, 2},
      {104, 2},
      {105, 2},
      {106, 2},
      {91, 2},
      {83, 5},
      {44, 3},
      {75, 3},
      {42, 3},
      {41, 2},
      {40, 2},
      {39, 2},
      {38, 2},
      {92, 2},
      {84, 5},
      {45, 3},
      {14, 3},
      {13, 3},
      {12, 2},
      {11, 2},
      {10, 2},
      {37, 2},
      {93, 2},
      {85, 5},
      {46, 5},
      {74, 5},
      {3, 3},
      {2, 2},
      {1, 2},
      {25, 2},
      {36, 2},
      {94, 2},
      {86, 4},
      {47, 4},
      {72, 4},
      {71, 4},
      {5, 2},
      {4, 2},
      {24, 2},
      {35, 2},
      {95, 2},
      {87, 4},
      {51, 1},
      {17, 1},
      {9, 0},
      {8, 0},
      {7, 0},
      {23, 0},
      {34, 0},
      {96, 2},
      {88, 4},
      {54, 1},
      {18, 1},
      {61, 0},
      {62, 0},
      {21, 0},
      {22, 0},
      {33, 0},
      {97, 0},
      {89, 0},
      {26, 1},
      {27, 0},
      {28, 0},
      {29, 0},
      {30, 0},
      {59, 0},
      {32, 0},
      {98, 0},
      {90, 0},
      {107, 1},
      {108, 1},
      {109, 1},
      {110, 1},
      {111, 1},
      {112, 1},
      {113, 1},
      {99, 0}
   };
   private static final String[] LEGEND_NAMES = new String[]{"EU-C", "EU-W", "NA-E", "NA-W", "Asia", "Oce"};
   private static final int[] REGION_COLORS = new int[]{
      packRgb(159, 206, 99), packRgb(0, 166, 99), packRgb(79, 173, 234), packRgb(47, 110, 186), packRgb(245, 194, 66), packRgb(252, 136, 3)
   };
   private static final int[] REGION_LEGEND_INDICES = new int[81];
   private HudScale hudScale;
   private float hudScaleRatio;
   private final IntegerSetting sizePercent;
   private final float[] cellNumberYs;
   private final EnumSetting placement = new EnumSetting(
      "Placement",
      "Where the map sits. Auto docks it beside your keystrokes (or top-right if keystrokes are off).",
      "Auto",
      "Auto",
      "Top Left",
      "Top Right",
      "Bottom Left",
      "Bottom Right"
   );
   private final float[] cellNumberWidths;
   private VertexTextureFont cachedFont;
   private final String[] cellNumberTexts;
   private final String[] legendLabelTexts;
   private static final int[] REGION_IDS = new int[81];
   private final IntegerSetting cellOpacity;

   private void renderLegend(int legendAsiaValue, int legendAsiaValue2, int legendIndexAt, float regionmapRegionDivisor) {
      byte legendAsiaFactor = 54;
      HudScale hudScale = this.hudScale;
      if (hudScale != null) {
         hudScale.handleAmountTranslate(MatrixPeekUtilsRender.getMatrixStack(), this.hudScaleRatio);
      }

      VertexConsumer quads = VertexGuiUtils.getQuads();

      for (int i = 0; i < LEGEND_NAMES.length; i++) {
         int regionFactor = legendAsiaValue + i % 2 * legendAsiaFactor;
         int legendAsiaValue6 = legendAsiaValue2 + i / 2 * 12;
         fillRect(quads, regionFactor, legendAsiaValue6 + 3.5F, 5.0F, 5.0F, 0xFF000000 | this.getLegendColor(i), regionmapRegionDivisor);
      }

      VertexGuiUtils.updateBufferClose();
      if (hudScale != null) {
         MatrixPeekUtilsRender.getMatrixStack().pop();
      }

      VertexTexturePeek regular = VertexTexturePeek.getRegular();
      VertexTexturePeek suisse = VertexTexturePeek.getSuisse();
      VertexTexturePeek vertexTexturePeek = regular.isCorzclientFlag() ? regular : (suisse.isCorzclientFlag() ? suisse : null);
      float legendAsiaValue7 = 11.0F;

      for (int j = 0; j < LEGEND_NAMES.length; j++) {
         int legendAsiaValue8 = legendAsiaValue + j % 2 * legendAsiaFactor;
         int brownValue = legendAsiaValue2 + j / 2 * 12;
         int legendAsiaValue10 = j == legendIndexAt ? -1 : -8224116;
         String formatAsiaLegend = this.getLegendLabel(j);
         if (vertexTexturePeek != null) {
            this.legendLabelTexts[j] = formatAsiaLegend;
            this.legendLabelXs[j] = legendAsiaValue8 + 8;
            this.legendLabelYs[j] = brownValue + (12.0F - vertexTexturePeek.getAmountForFloat(11.0F)) / 2.0F;
            this.legendLabelColors[j] = legendAsiaValue10;
         } else {
            VertexTextureFont font = HudFontUtils.getSmallFont();
            font.handle(formatAsiaLegend, legendAsiaValue8 + 8, font.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, 12), new Color(legendAsiaValue10, true));
         }
      }

      if (vertexTexturePeek != null) {
         vertexTexturePeek.handlePeekEnd(this.legendLabelTexts, this.legendLabelXs, this.legendLabelYs, this.legendLabelColors, LEGEND_NAMES.length, 11.0F);
      }
   }

   private static int withAlpha(int regionValue, float regionLimit) {
      int round = Math.round(clampForDouble(regionLimit) * 255.0F);
      return round << 24 | regionValue & 16777215;
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      this.renderScaledWindow(tickProgress.drawContext);
   }

   private void renderRegionMapPanel(DrawContext context, int regionmapRegionValue, int scaledHeight, float regionmapRegionDivisor, float rocketsElytraBound) {
      int ceil3 = (int)Math.ceil(LEGEND_NAMES.length / 2.0);
      float nativeHudValue = 1.0F / Math.max(0.05F, rocketsElytraBound);
      int round5 = 15;
      int regionmapRegionBound = 120;
      boolean regionmapRegionFlag = ShowHUD.isFlag();
      if (regionmapRegionFlag) {
         round5 = Math.round(NativeHud.getAmountFloat(nativeHudValue));
         regionmapRegionBound = Math.max(regionmapRegionBound, (int)Math.ceil(NativeHud.getAmount("Region Map", nativeHudValue)));
      }

      int regionmapRegionTotal = round5 + 3 + 108 + 6;
      if (this.legend.isEnabled()) {
         regionmapRegionTotal += 5 + ceil3 * 12;
      }

      byte aggroPreemptBound = 4;
      String formatText = this.placement.getCurrentOption();
      byte regionmapRegionValue6 = -1;
      switch (formatText.hashCode()) {
         case -960744398:
            if (formatText.equals("Top Left")) {
               regionmapRegionValue6 = 0;
            }
            break;
         case 287355729:
            if (formatText.equals("Top Right")) {
               regionmapRegionValue6 = 3;
            }
            break;
         case 926500839:
            if (formatText.equals("Bottom Right")) {
               regionmapRegionValue6 = 2;
            }
            break;
         case 999535836:
            if (formatText.equals("Bottom Left")) {
               regionmapRegionValue6 = 1;
            }
      }

      int regionmapRegionLimit;
      int intersectsCenterBound;

      switch (regionmapRegionValue6) {
         case 0 -> {
            regionmapRegionLimit = aggroPreemptBound;
            intersectsCenterBound = aggroPreemptBound;
         }
         case 1 -> {
            regionmapRegionLimit = aggroPreemptBound;
            intersectsCenterBound = scaledHeight - regionmapRegionTotal - aggroPreemptBound;
         }
         case 2 -> {
            regionmapRegionLimit = regionmapRegionValue - regionmapRegionBound - aggroPreemptBound;
            intersectsCenterBound = scaledHeight - regionmapRegionTotal - aggroPreemptBound;
         }
         case 3 -> {
            regionmapRegionLimit = regionmapRegionValue - regionmapRegionBound - aggroPreemptBound;
            intersectsCenterBound = aggroPreemptBound + Math.round(ShowHUD.getBeneficialEffects() / regionmapRegionDivisor);
         }
         default -> {
            float regionmapRegionFactor = 100.0F / Math.max(1, this.sizePercent.getValue());
            if (ShowHUD.isSsaPerfgraphCountRoundSsa()) {
               regionmapRegionLimit = Math.round(ShowHUD.roundSsaTop * regionmapRegionFactor) - aggroPreemptBound - regionmapRegionBound;
               intersectsCenterBound = Math.round(ShowHUD.round * regionmapRegionFactor);
            } else if (ShowHUD.isPotionsSsa()) {
               float regionmapRegionFactor2 = 100.0F / Math.max(1, this.sizePercent.getValue());
               int round = Math.round(ShowHUD.regionmapRegionValue * regionmapRegionFactor2);
               int round8 = Math.round(ShowHUD.ssaPotionsOffsetX * regionmapRegionFactor2);
               int round9 = Math.round((ShowHUD.regionmapRegionValue + ShowHUD.potionsHudOffset) * regionmapRegionFactor2);
               int round10 = Math.round((ShowHUD.ssaPotionsOffsetX + ShowHUD.ssaPotionsValue4SsaPotions) * regionmapRegionFactor2);
               regionmapRegionLimit = round9 + aggroPreemptBound;
               intersectsCenterBound = round8;
               if (regionmapRegionLimit + regionmapRegionBound > regionmapRegionValue - aggroPreemptBound) {
                  regionmapRegionLimit = round;
                  intersectsCenterBound = round10 + aggroPreemptBound;
               }

               if (ShowHUD.isSsaPerfgraphCountPotionsSsa()) {
                  int round11 = Math.round(ShowHUD.radarSsaTotalRadarSsa * regionmapRegionFactor2);
                  int round12 = Math.round(ShowHUD.radarSsaTotal2RadarSsa * regionmapRegionFactor2);
                  int round2 = Math.round((ShowHUD.radarSsaTotalRadarSsa + ShowHUD.radarSsaFactorRegionmapRegio) * regionmapRegionFactor2);
                  int round3 = Math.round((ShowHUD.radarSsaTotal2RadarSsa + ShowHUD.radarSsaFactorRadarSsa) * regionmapRegionFactor2);
                  if (regionmapRegionLimit < round2 && regionmapRegionLimit + regionmapRegionBound > round11 && intersectsCenterBound < round3 && intersectsCenterBound + regionmapRegionTotal > round12) {
                     if (round2 + aggroPreemptBound + regionmapRegionBound <= regionmapRegionValue - aggroPreemptBound) {
                        regionmapRegionLimit = round2 + aggroPreemptBound;
                        intersectsCenterBound = round12;
                     } else {
                        if (regionmapRegionLimit > round) {
                           regionmapRegionLimit = round11;
                        }

                        intersectsCenterBound = round3 + aggroPreemptBound;
                     }
                  }
               }
            } else {
               regionmapRegionLimit = regionmapRegionValue - regionmapRegionBound - aggroPreemptBound;
               intersectsCenterBound = aggroPreemptBound + Math.round(ShowHUD.getBeneficialEffects() / regionmapRegionDivisor);
            }
         }
      }

      if (ShowHUD.isPotionsSsaHudValue()) {
         float regionmapRegionFactor3 = 100.0F / Math.max(1, this.sizePercent.getValue());
         int round13 = Math.round(ShowHUD.ssaPotionsOffsetY * regionmapRegionFactor3);
         int round14 = Math.round(ShowHUD.ssaPotionsValue * regionmapRegionFactor3);
         int round4 = Math.round((ShowHUD.ssaPotionsOffsetY + ShowHUD.hudValue) * regionmapRegionFactor3);
         int regionmapRegionLimit5 = Math.round((ShowHUD.ssaPotionsValue + ShowHUD.ssaPotionsExtraSize) * regionmapRegionFactor3);
         if (regionmapRegionLimit < round4 && regionmapRegionLimit + regionmapRegionBound > round13 && intersectsCenterBound < regionmapRegionLimit5 && intersectsCenterBound + regionmapRegionTotal > round14) {
            intersectsCenterBound = regionmapRegionLimit5 + aggroPreemptBound;
         }
      }

      int max4 = Math.max(aggroPreemptBound, Math.min(regionmapRegionLimit, regionmapRegionValue - regionmapRegionBound - aggroPreemptBound));
      int max5 = Math.max(aggroPreemptBound, Math.min(intersectsCenterBound, scaledHeight - regionmapRegionTotal - aggroPreemptBound));
      float amount4 = ShowHUD.getAmount4("regionmap");
      float amountFloat = ShowHUD.getAmountFloat("regionmap");
      int colorValue = max4 + Math.round(amount4 / rocketsElytraBound);
      int glfwLimit = max5 + Math.round(amountFloat / rocketsElytraBound);
      HudScale hudScale = ShowHUD.getHudHotbar("regionmap", max4 * rocketsElytraBound + amount4, max5 * rocketsElytraBound + amountFloat, regionmapRegionBound * rocketsElytraBound, regionmapRegionTotal * rocketsElytraBound);
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      this.hudScale = hudScale;
      this.hudScaleRatio = regionmapRegionDivisor / rocketsElytraBound;
      if (hudScale != null) {
         hudScale.handleAmountTranslateForMatrixStackFloat(matrixStack, 1.0F / rocketsElytraBound);
      }

      if (regionmapRegionFlag) {
         NativeHud.handleColor(colorValue, glfwLimit, regionmapRegionBound, regionmapRegionTotal, IcoGenUtils.getIdentifierForString("pin"), "Region Map", nativeHudValue);
      } else {
         ShowHUD.getAmount(context, colorValue, glfwLimit, regionmapRegionBound, regionmapRegionTotal, "Region Map");
      }

      double x = FakeCoordinates.getX(mc.player.getX());
      double z = FakeCoordinates.getZ(mc.player.getZ());
      int legendIndexAt = this.getLegendIndexAt(x, z);
      int legendPopValue = colorValue + (regionmapRegionBound - 108) / 2;
      int regionmapRegionValue21 = glfwLimit + round5 + 3;
      this.renderGrid(legendPopValue, regionmapRegionValue21, x, z, regionmapRegionDivisor);
      if (this.showCellNumbers.isEnabled()) {
         this.renderCellNumbers(legendPopValue, regionmapRegionValue21);
      }

      if (this.legend.isEnabled()) {
         this.renderLegend(legendPopValue, regionmapRegionValue21 + 108 + 5, legendIndexAt, regionmapRegionDivisor);
      }

      if (hudScale != null) {
         matrixStack.pop();
      }
   }

   public RegionMap() {
      super("RegionMap", IconsPngType.DONUTSMP);
      this.sizePercent = new IntegerSetting(
         "Size",
         "Overall panel size, as a percentage. Lower = smaller: shrinks the whole map (grid, header, legend and arrow together) so it fits the HUD. 100% is the old size; ~50% is about keystroke-sized.",
         60,
         40,
         120,
         5
      );
      this.cellOpacity = new IntegerSetting("Opacity", "Region cell fill opacity.", 90, 10, 100, 5);
      this.showCellNumbers = new BooleanSetting("Cell Numbers", "Show each region's ID number (needs a bigger map to read).", false);
      this.legend = new BooleanSetting("Legend", "Show the region-type colour legend below the grid.", true);
      this.hudScaleRatio = 1.0F;
      this.cellNumberWidths = new float[81];
      this.cellNumberTexts = new String[81];
      this.cellNumberXs = new float[81];
      this.cellNumberYs = new float[81];
      this.legendLabelTexts = new String[LEGEND_NAMES.length];
      this.legendLabelXs = new float[LEGEND_NAMES.length];
      this.legendLabelYs = new float[LEGEND_NAMES.length];
      this.legendLabelColors = new int[LEGEND_NAMES.length];
      this.addSettings(new ConfigSetting[]{this.placement, this.sizePercent, this.cellOpacity, this.showCellNumbers, this.legend});
   }

   private int getLegendColor(int index) {
      return index >= 0 && index < REGION_COLORS.length ? REGION_COLORS[index] : 16777215;
   }

   private int[] getCellIndices(double regionValue, double z) {
      int floor3 = (int)Math.floor((regionValue + 225000.0) / 50000.0);
      int floor4 = (int)Math.floor((z + 225000.0) / 50000.0);
      return new int[]{floor3, floor4};
   }

   private static float clampFloat(float regionLimit, float regionLimit2, float regionValue) {
      return regionLimit < regionLimit2 ? regionLimit2 : (regionLimit > regionValue ? regionValue : regionLimit);
   }

   private static void fillRect(VertexConsumer vertices, float regionFactor, float regionFactor2, float regionValue, float regionValue2, int panelOverlayValue, float regionmapRegionDivisor) {
      VertexGuiUtils.handleVertexColor(vertices, regionFactor * regionmapRegionDivisor, regionFactor2 * regionmapRegionDivisor, (regionFactor + regionValue) * regionmapRegionDivisor, (regionFactor2 + regionValue2) * regionmapRegionDivisor, CorzRoundedUtils.getPanelOverlay(panelOverlayValue));
   }

   static {
      for (int i = 0; i < REGION_TABLE_ROWS.length && i < REGION_IDS.length; i++) {
         if (REGION_TABLE_ROWS[i].length >= 2) {
            REGION_IDS[i] = REGION_TABLE_ROWS[i][0];
            REGION_LEGEND_INDICES[i] = Math.min(REGION_TABLE_ROWS[i][1], LEGEND_NAMES.length - 1);
         } else {
            REGION_LEGEND_INDICES[i] = -1;
         }
      }

      for (int j = 0; j < REGION_ID_TEXTS.length; j++) {
         REGION_ID_TEXTS[j] = String.valueOf(REGION_IDS[j]);
      }
   }

   private int getLegendIndexAt(double regionValue, double z) {
      int[] regionValues = this.getCellIndices(regionValue, z);
      return regionValues[0] >= 0 && regionValues[0] < 9 && regionValues[1] >= 0 && regionValues[1] < 9 ? this.getLegendIndexForCell(regionValues[1] * 9 + regionValues[0]) : -1;
   }

   private void drawPlayerArrow(VertexConsumer vertices, float regionValue, float regionValue2, float regionValue3, int panelOverlayValue, float regionmapRegionDivisor) {
      double angle = Math.toRadians(regionValue3 + 180.0);
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      this.fillRotatedTriangle(vertices, regionValue, regionValue2, cos, sin, 0.0F, -4.9F, 3.3F, 2.9F, -3.3F, 2.9F, withAlpha(0, 0.55F), regionmapRegionDivisor);
      this.fillRotatedTriangle(vertices, regionValue, regionValue2, cos, sin, 0.0F, -4.0F, 2.6F, 2.2F, -2.6F, 2.2F, panelOverlayValue, regionmapRegionDivisor);
   }

   private static float clampForDouble(double regionLimit) {
      return (float)(regionLimit < 0.0 ? 0.0 : (regionLimit > 1.0 ? 1.0 : regionLimit));
   }

   private static int packRgb(int regionValue, int regionValue2, int regionValue3) {
      return regionValue << 16 | regionValue2 << 8 | regionValue3;
   }

   private int getLegendIndexForCell(int index2) {
      return index2 >= 0 && index2 < REGION_LEGEND_INDICES.length ? REGION_LEGEND_INDICES[index2] : -1;
   }

   private double[] getCellFraction(double regionValue, double z) {
      double regionCounter = (regionValue + 225000.0) % 50000.0 / 50000.0;
      double regionLimit = (z + 225000.0) % 50000.0 / 50000.0;
      if (regionCounter < 0.0) {
         regionCounter++;
      }

      if (regionLimit < 0.0) {
         regionLimit++;
      }

      return new double[]{clampForDouble(regionCounter), clampForDouble(regionLimit)};
   }

   public void renderScaledWindow(DrawContext context) {
      if (mc.player != null && mc.world != null) {
         if (context != null) {
            MatrixStack matrixStack = new MatrixStack();
            CorzClient.fontManager.setMatrixStack(matrixStack);
            MatrixPeekUtilsRender.handleMatrixStack(matrixStack);
            double scaleFactor = mc.getWindow().getScaleFactor();
            int calculateScaleFactor = mc.getWindow().calculateScaleFactor(ShowHUD.getMax(mc), mc.forcesUnicodeFont());
            float scaledWindowFactor = Math.max(1, this.sizePercent.getValue()) / 100.0F;
            float scaledWindowDivisor = (float)(calculateScaleFactor / scaleFactor) * scaledWindowFactor;
            boolean scaledWindowFlag = Math.abs(scaledWindowDivisor - 1.0F) > 1.0E-4F;
            int scaledWidth;
            int scaledHeight;
            if (scaledWindowFlag) {
               scaledWidth = Math.round(mc.getWindow().getScaledWidth() / scaledWindowDivisor);
               scaledHeight = Math.round(mc.getWindow().getScaledHeight() / scaledWindowDivisor);
               CorzRoundedUtils.scaledWidth = scaledWidth;
               CorzRoundedUtils.scaledHeight = scaledHeight;
            } else {
               scaledWidth = mc.getWindow().getScaledWidth();
               scaledHeight = mc.getWindow().getScaledHeight();
            }

            float scaledWindowValue = ShowHUD.getTime();
            if (scaledWindowValue < 0.004F) {
               if (scaledWindowFlag) {
                  CorzRoundedUtils.scaledWidth = 0.0F;
                  CorzRoundedUtils.scaledHeight = 0.0F;
               }
            } else {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(scaledWindowValue);

               try {
                  this.renderRegionMapPanel(context, scaledWidth, scaledHeight, scaledWindowDivisor, scaledWindowFactor);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
                  if (scaledWindowFlag) {
                     CorzRoundedUtils.scaledWidth = 0.0F;
                     CorzRoundedUtils.scaledHeight = 0.0F;
                  }
               }
            }
         }
      }
   }

   private void renderCellNumbers(int legendRegionValue, int legendRegionValue2) {
      VertexTextureFont vertexTexture = HudFontUtils.getSmallFont();
      if (vertexTexture != this.cachedFont) {
         for (int i = 0; i < REGION_ID_TEXTS.length; i++) {
            this.cellNumberWidths[i] = vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(REGION_ID_TEXTS[i]);
         }

         this.cachedFont = vertexTexture;
      }

      int index = 0;

      for (int j = 0; j < 9; j++) {
         int abcdefghijklmnopqrstuvwxyzBrown = vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown(legendRegionValue2 + j * 12, 12);

         for (int k = 0; k < 9; k++) {
            int legendRegionLimit = this.getLegendIndexForCell(j * 9 + k);
            if (legendRegionLimit >= 0) {
               float legendRegionValue4 = this.cellNumberWidths[j * 9 + k];
               this.cellNumberTexts[index] = REGION_ID_TEXTS[j * 9 + k];
               this.cellNumberXs[index] = legendRegionValue + k * 12 + (12.0F - legendRegionValue4) / 2.0F - 1.5F;
               this.cellNumberYs[index] = abcdefghijklmnopqrstuvwxyzBrown;
               index++;
            }
         }
      }

      vertexTexture.handleFlag(this.cellNumberTexts, this.cellNumberXs, this.cellNumberYs, index, OFF_WHITE);
   }

   private void fillRotatedTriangle(
      VertexConsumer vertices, float regionValue, float regionValue2, float regionFactor, float regionFactor2, float regionFactor3, float regionFactor4, float regionFactor5, float regionFactor6, float regionFactor7, float regionFactor8, int panelOverlayValue, float regionmapRegionDivisor
   ) {
      float vertexColorValue = (regionValue + (regionFactor3 * regionFactor - regionFactor4 * regionFactor2)) * regionmapRegionDivisor;
      float regionValue5 = (regionValue2 + (regionFactor3 * regionFactor2 + regionFactor4 * regionFactor)) * regionmapRegionDivisor;
      float regionValue6 = (regionValue + (regionFactor5 * regionFactor - regionFactor6 * regionFactor2)) * regionmapRegionDivisor;
      float regionValue7 = (regionValue2 + (regionFactor5 * regionFactor2 + regionFactor6 * regionFactor)) * regionmapRegionDivisor;
      float regionValue8 = (regionValue + (regionFactor7 * regionFactor - regionFactor8 * regionFactor2)) * regionmapRegionDivisor;
      float regionValue9 = (regionValue2 + (regionFactor7 * regionFactor2 + regionFactor8 * regionFactor)) * regionmapRegionDivisor;
      VertexGuiUtils.handleVertexColor(vertices, vertexColorValue, regionValue5, regionValue6, regionValue7, regionValue8, regionValue9, regionValue8, regionValue9, CorzRoundedUtils.getPanelOverlay(panelOverlayValue));
   }

   private void renderGrid(int legendPopValue, int legendPopValue2, double regionValue, double z, float regionmapRegionDivisor) {
      float regionLimit = clampForDouble(this.cellOpacity.getValue() / 100.0F);
      int[] legendPopValues = this.getCellIndices(regionValue, z);
      boolean legendPopFlag = legendPopValues[0] >= 0 && legendPopValues[0] < 9 && legendPopValues[1] >= 0 && legendPopValues[1] < 9;
      HudScale hudScale = this.hudScale;
      if (hudScale != null) {
         hudScale.handleAmountTranslate(MatrixPeekUtilsRender.getMatrixStack(), this.hudScaleRatio);
      }

      VertexConsumer quads = VertexGuiUtils.getQuads();
      fillRect(quads, legendPopValue, legendPopValue2, 108.0F, 108.0F, withAlpha(0, 0.45F), regionmapRegionDivisor);

      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            int legendPopLimit = this.getLegendIndexForCell(i * 9 + j);
            if (legendPopLimit >= 0) {
               fillRect(quads, legendPopValue + j * 12 + 1, legendPopValue2 + i * 12 + 1, 10.0F, 10.0F, withAlpha(this.getLegendColor(legendPopLimit), regionLimit), regionmapRegionDivisor);
            }
         }
      }

      if (legendPopFlag) {
         int legendPopValue7 = legendPopValue + legendPopValues[0] * 12;
         int legendPopValue8 = legendPopValue2 + legendPopValues[1] * 12;
         fillRect(quads, legendPopValue7 + 1, legendPopValue8 + 1, 10.0F, 10.0F, withAlpha(16777215, 0.34F), regionmapRegionDivisor);
      }

      if (legendPopFlag) {
         double[] cellFraction = this.getCellFraction(regionValue, z);
         float clampFloat9 = clampFloat(legendPopValue + (float)((legendPopValues[0] + cellFraction[0]) * 12.0), legendPopValue + 5, legendPopValue + 108 - 5);
         float clampFloat10 = clampFloat(legendPopValue2 + (float)((legendPopValues[1] + cellFraction[1]) * 12.0), legendPopValue2 + 5, legendPopValue2 + 108 - 5);
         this.drawPlayerArrow(quads, clampFloat9, clampFloat10, mc.player.getYaw(), -1, regionmapRegionDivisor);
      }

      VertexGuiUtils.updateBufferClose();
      if (hudScale != null) {
         MatrixPeekUtilsRender.getMatrixStack().pop();
      }
   }

   private String getLegendLabel(int index) {
      return index >= 0 && index < LEGEND_NAMES.length ? LEGEND_NAMES[index] : "";
   }
}
