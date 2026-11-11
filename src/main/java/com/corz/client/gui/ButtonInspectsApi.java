package com.corz.client.gui;

import net.minecraft.client.gui.DrawContext;
import com.corz.client.utils.ScannedSkipped;

public interface ButtonInspectsApi {
   default boolean isClericLimits() {
      return false;
   }

   default boolean canAmountMatrix(ButtonMin buttonMin, double matrixValue, double amountMaxForDouble, double consumerLimit) {
      return false;
   }

   default String formatShowOrigin() {
      return null;
   }

   default String formatTextsInspects(int inspectsValue) {
      return "";
   }

   default void handleThumbsAmount(float thumbsValue) {
   }

   default boolean isFlag() {
      return false;
   }

   default boolean canMatrixStack(ButtonMin buttonMin, double matrixStackValue, double amountMaxForDouble, int matrixStackValue3) {
      return false;
   }

   default float getMax() {
      return 118.0F;
   }

   default boolean canMaxSubstring(int substringValue, int substringValue2) {
      return false;
   }

   default boolean canMaxAmount(ScannedSkipped scannedSkipped, double buttonInspectsValue, double amountMaxForDouble, double consumerLimit) {
      return false;
   }

   default void renderThumbsInspects(DrawContext context, int thumbsInspectsValue, float thumbsOffset, float matrixStackValue, float thumbsInspectsValue4) {
   }

   default boolean canMatrixStack(int matrixStackValue, int matrixStackValue2) {
      return false;
   }

   default void handleFolderDone(PreviewText previewText) {
   }

   default void renderCubeAmount(DrawContext context, ButtonMin buttonMin) {
   }

   default float getAmount() {
      return 320.0F;
   }

   default int getShowSchematics() {
      return 0;
   }

   default String formatSoldBought(int soldBoughtValue) {
      return null;
   }

   default float getSchematicMaterialsAmount() {
      return 460.0F;
   }

   default void renderSkippedLim(DrawContext context, ScannedSkipped scannedSkipped) {
   }

   default void renderCubeAmountForDrawContextButtonMin(DrawContext context, ButtonMin buttonMin) {
   }

   default boolean isShowFlag() {
      return false;
   }

   default void handleMatrixParse(ButtonMin buttonMin, double matrixParseValue, double matrixParseValue2, double matrixParseValue3, double matrixParseValue4, int matrixParseValue5) {
   }

   default void handleMatrixStack(ButtonMin buttonMin, double matrixStackValue, double matrixStackValue2, int button) {
   }

   void renderCubeSchematic(DrawContext context, ButtonMin buttonMin);

   default void updateShowFlag() {
   }

   default String formatSchematicsText() {
      return null;
   }

   default void handleInspectsSchematics(int inspectsSchematicsValue) {
   }

   String formatText();

   default float getSettingsCleric() {
      return 0.0F;
   }
}
