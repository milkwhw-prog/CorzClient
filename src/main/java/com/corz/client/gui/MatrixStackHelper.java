package com.corz.client.gui;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.KeybindSetting;
import com.corz.client.config.StringBase;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.ListeningGlfw;
import com.corz.client.render.MatrixStackUtilsAmount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import com.corz.client.utils.AmountPreviewUtils;
import com.corz.client.utils.GlfwListeningApi;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public final class MatrixStackHelper implements GlfwListeningApi {
   private float panelY;
   private final Map<String, Float> cubeFloatMap;
   private float cubeAmount;
   private float targetScroll;
   private float cubeSchematicAmount;
   private final Map<String, Boolean> matrixStackBooleanMap;
   private float matrixStackAmount;
   private final Map<String, QuintScissor> matrixStackQuintScissorMap;
   private float glfwAmount;
   private final Map<String, float[]> matrixStackFloatArrayMap;
   private final List<MatrixStackHelper.Row> cubeRowList = new ArrayList<>();
   private ListeningGlfw listeningGlfw;
   private MatrixStackHelper.Row next;
   private final Map<String, Float> glfwFloatMap;
   private float viewportHeight;

   private static void handle(MatrixStackHelper.Row row, int matrixStackValue, int y, int width) {
      Object obj = row.glfwObject;
      if (obj instanceof RenderBeta renderBeta) {
         renderBeta.handle(matrixStackValue, y, width, row.rowHeight);
      } else if (obj instanceof RendererPress rendererPress) {
         rendererPress.handle(matrixStackValue, y, width, row.rowHeight);
      } else if (obj instanceof IntegerFlag integerFlag) {
         integerFlag.handle(matrixStackValue, y, width, row.rowHeight);
      } else if (obj instanceof FlagHandle flagHandle) {
         flagHandle.handle(matrixStackValue, y, width, row.rowHeight);
      } else if (obj instanceof ScissorDisable scissorDisable) {
         scissorDisable.handle(matrixStackValue, y, width, row.rowHeight);
      } else if (obj instanceof MaxRenderer maxRenderer) {
         maxRenderer.handle(matrixStackValue, y, width, row.rowHeight);
      } else if (obj instanceof ListeningGlfw listeningGlfw) {
         listeningGlfw.handleListening(matrixStackValue, y, width, row.rowHeight);
      }
   }

   private static void render(DrawContext context, MatrixStackHelper.Row row, int matrixStackValue, int mouseY) {
      Object obj = row.glfwObject;
      if (obj instanceof RenderBeta renderBeta) {
         renderBeta.renderTimeRenderer(context, matrixStackValue, mouseY);
      } else if (obj instanceof RendererPress rendererPress) {
         rendererPress.renderTimeRenderer(context, matrixStackValue, mouseY);
      } else if (obj instanceof IntegerFlag integerFlag) {
         integerFlag.renderRendererFont(context, matrixStackValue, mouseY);
      } else if (obj instanceof FlagHandle flagHandle) {
         flagHandle.renderTimeRenderer(context, matrixStackValue, mouseY);
      } else if (obj instanceof ScissorDisable scissorDisable) {
         scissorDisable.renderTextTime(context, matrixStackValue, mouseY);
      } else if (obj instanceof MaxRenderer maxRenderer) {
         maxRenderer.renderMaxRelease(context, matrixStackValue, mouseY);
      } else if (obj instanceof ListeningGlfw listeningGlfw) {
         listeningGlfw.renderListeningRelease(context, matrixStackValue, mouseY);
      }
   }

   private static void handleTranslatePop(Identifier id, float translatePopValue, float roundOffset, float translatePopValue3, int matrixStackValue) {
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      float degreesOffset = translatePopValue + 4.0F;
      float argbOffset = roundOffset + 4.0F;
      matrixStack.push();

      try {
         matrixStack.translate(degreesOffset, argbOffset, 0.0F);
         matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(translatePopValue3));
         matrixStack.translate(-degreesOffset, -argbOffset, 0.0F);
         MatrixStackUtilsRelease.handle(id, translatePopValue, roundOffset, 8.0F, matrixStackValue);
      } finally {
         matrixStack.pop();
      }
   }

   public void renderAmountCube(DrawContext context, ButtonMin buttonMin) {
      this.glfwAmount = buttonMin.cubeKnifeAmount + 10.0F;
      this.cubeSchematicAmount = buttonMin.cubeSchematicAmount2Cube - 20.0F;
      this.panelY = buttonMin.cubeSchematicAmount + 6.0F;
      this.viewportHeight = buttonMin.cubeSchematicAmount3Cube - 12.0F;
      this.matrixStackAmount = AmountPreviewUtils.getAmount(this.matrixStackAmount, this.targetScroll, 18.0F, buttonMin.frameDelta, 0.05F);
      this.cubeAmount = this.getAmount();
      this.updateMaxAmount();
      int roundMaterialAmount = Math.round(buttonMin.materialAmount);
      int cubeLimit = Math.round(buttonMin.cursorY);
      boolean thumbsActive = buttonMin.thumbsActive;
      float deltaAmount = this.panelY - MatrixStackUtilsAmount.getAmount(this.matrixStackAmount);
      this.glfwFloatMap.clear();
      int index = 0;
      int itemsSize = this.cubeRowList.size();

      while (index < itemsSize) {
         String cubeText = this.cubeRowList.get(index).sectionTitle;
         int i = index;
         int cubeTotal = 0;

         while (i < itemsSize && this.cubeRowList.get(i).sectionTitle.equals(cubeText)) {
            cubeTotal += this.cubeRowList.get(i++).rowHeight;
         }

         float orDefault = this.cubeFloatMap.getOrDefault(cubeText, 0.0F);
         this.glfwFloatMap.put(cubeText, deltaAmount);
         this.renderAmount(
            context, cubeText, deltaAmount, orDefault, thumbsActive && cubeLimit >= deltaAmount && cubeLimit < deltaAmount + 20.0F && roundMaterialAmount >= this.glfwAmount && roundMaterialAmount <= this.glfwAmount + this.cubeSchematicAmount, buttonMin.frameDelta
         );
         float cubeValue = deltaAmount + 20.0F;
         int round = Math.round(cubeTotal * orDefault);
         this.renderCubeSchematic(context, buttonMin, index, i, cubeValue, round, orDefault, thumbsActive ? roundMaterialAmount : -10000, thumbsActive ? cubeLimit : -10000);
         deltaAmount = cubeValue + round;
         index = i;
      }
   }

   public void handle(double matrixStackValue, double mouseReleasedValue, int button) {
      if (this.next != null) {
         if (this.next.glfwObject instanceof IntegerFlag integerFlag) {
            integerFlag.handleFlag(matrixStackValue, mouseReleasedValue, button);
         } else if (this.next.glfwObject instanceof FlagHandle flagHandle) {
            flagHandle.handleFlag(matrixStackValue, mouseReleasedValue, button);
         }

         this.next = null;
      }
   }

   private void renderCubeSchematic(DrawContext context, ButtonMin buttonMin, int cubeSchematicValue, int cubeSchematicLimit, float cubeSchematicBound, int round, float panelOverlayLimit, int cubeSchematicValue2, int mouseY) {
      boolean flag = panelOverlayLimit >= 1.0F;
      float amount = flag ? cubeSchematicBound : cubeSchematicBound - Math.round(8.0F * (1.0F - panelOverlayLimit));

      for (int i = cubeSchematicValue; i < cubeSchematicLimit; i++) {
         MatrixStackHelper.Row row = this.cubeRowList.get(i);
         row.rowY = amount;
         row.cubeSchematicActive = flag;
         amount += row.rowHeight;
      }

      if (round > 0) {
         if (flag) {
            for (int j = cubeSchematicValue; j < cubeSchematicLimit; j++) {
               this.renderAmount(context, this.cubeRowList.get(j), buttonMin.cubeSchematicAmount, buttonMin.cubeSchematicAmount + buttonMin.cubeSchematicAmount3Cube, cubeSchematicValue2, mouseY);
            }
         } else {
            MatrixStackUtilsRelease.renderScissorEnable(context, this.glfwAmount - 4.0F, cubeSchematicBound, this.cubeSchematicAmount + 8.0F, round);
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

            try {
               float maxCubeSchematicAmount = Math.max(buttonMin.cubeSchematicAmount, cubeSchematicBound);
               float matrixStackLimit = Math.min(buttonMin.cubeSchematicAmount + buttonMin.cubeSchematicAmount3Cube, cubeSchematicBound + round);

               for (int k = cubeSchematicValue; k < cubeSchematicLimit; k++) {
                  this.renderAmount(context, this.cubeRowList.get(k), maxCubeSchematicAmount, matrixStackLimit, -10000, -10000);
               }
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               context.disableScissor();
            }
         }
      }
   }

   public String formatAmount(int matrixStackValue) {
      int matrixStackCounter = 0;

      for (Entry entry : this.glfwFloatMap.entrySet()) {
         if (matrixStackCounter++ == matrixStackValue) {
            float glfwLimit = this.glfwAmount + this.cubeSchematicAmount / 2.0F;
            float mouseY = (Float)entry.getValue() + 10.0F;
            boolean amountGlfw = this.canAmountGlfw(glfwLimit, mouseY, 0);
            return (String)entry.getKey() + " at " + Math.round(glfwLimit) + "," + Math.round(mouseY) + " hit=" + amountGlfw + " open=" + this.matrixStackBooleanMap.get(entry.getKey());
         }
      }

      return "no section " + matrixStackValue + " of " + this.glfwFloatMap.size();
   }

   public MatrixStackHelper(Map<String, List<ConfigSetting>> map) {
      this.matrixStackBooleanMap = new LinkedHashMap<>();
      this.matrixStackQuintScissorMap = new HashMap<>();
      this.matrixStackFloatArrayMap = new HashMap<>();
      this.cubeFloatMap = new HashMap<>();
      this.glfwFloatMap = new LinkedHashMap<>();
      boolean matrixStackFlag = true;

      for (Entry entry : map.entrySet()) {
         this.matrixStackBooleanMap.put((String)entry.getKey(), matrixStackFlag);
         this.matrixStackQuintScissorMap.put((String)entry.getKey(), new QuintScissor(QuintBounceType.CUBIC_OUT, 200, matrixStackFlag ? 1.0 : 0.0));
         matrixStackFlag = false;

         for (ConfigSetting autoEspBase : (Iterable<com.corz.client.config.ConfigSetting>) ((List)entry.getValue())) {
            MatrixStackHelper.Row classForStringConfigSetting = this.getClassForStringConfigSetting((String)entry.getKey(), autoEspBase);
            if (classForStringConfigSetting != null) {
               this.cubeRowList.add(classForStringConfigSetting);
            }
         }
      }
   }

   private void updateMaxAmount() {
      float max = Math.max(0.0F, this.cubeAmount - this.viewportHeight);
      if (this.targetScroll > max) {
         this.targetScroll = max;
      }

      if (this.targetScroll < 0.0F) {
         this.targetScroll = 0.0F;
      }

      if (this.matrixStackAmount > max) {
         this.matrixStackAmount = max;
      }
   }

   private void handle(Object obj) {
      for (MatrixStackHelper.Row row : this.cubeRowList) {
         if (row.glfwObject instanceof MaxRenderer maxRenderer && maxRenderer != obj && maxRenderer.isFlag()) {
            maxRenderer.handleMaxTime(false);
         }
      }
   }

   public boolean canAmountGlfw(double glfwLimit, double mouseY, int glfwValue) {
      if (this.listeningGlfw != null) {
         ListeningGlfw listeningGlfw = this.listeningGlfw;
         this.listeningGlfw = null;
         listeningGlfw.handle(glfwValue);
         return true;
      } else if (!(glfwLimit < this.glfwAmount - 4.0F) && !(glfwLimit > this.glfwAmount + this.cubeSchematicAmount + 4.0F) && !(mouseY < this.panelY) && !(mouseY > this.panelY + this.viewportHeight)
         )
       {
         for (Entry entry : this.glfwFloatMap.entrySet()) {
            float glfwValue2 = (Float)entry.getValue();
            if (mouseY >= glfwValue2 && mouseY < glfwValue2 + 20.0F) {
               if (glfwValue == 0) {
                  this.handleFlag((String)entry.getKey());
               }

               this.handle(null);
               return true;
            }
         }

         for (MatrixStackHelper.Row next : this.cubeRowList) {
            if (next.cubeSchematicActive && !(mouseY < next.rowY) && !(mouseY >= next.rowY + next.rowHeight)) {
               this.handle(next.glfwObject);
               if (!canFlag(next, glfwLimit, mouseY, glfwValue)) {
                  return false;
               }

               if (next.glfwObject instanceof IntegerFlag || next.glfwObject instanceof FlagHandle) {
                  this.next = next;
               }

               return true;
            }
         }

         this.handle(null);
         return false;
      } else {
         return false;
      }
   }

   private float getAmount() {
      float matrixStackTotal = 0.0F;
      this.cubeFloatMap.clear();
      int index = 0;
      int itemsSize = this.cubeRowList.size();

      while (index < itemsSize) {
         String matrixStackText = this.cubeRowList.get(index).sectionTitle;
         int i = index;
         int matrixStackTotal2 = 0;

         while (i < itemsSize && this.cubeRowList.get(i).sectionTitle.equals(matrixStackText)) {
            matrixStackTotal2 += this.cubeRowList.get(i++).rowHeight;
         }

         float matrixStackFactor = this.getAmount(matrixStackText);
         this.cubeFloatMap.put(matrixStackText, matrixStackFactor);
         matrixStackTotal += 20 + Math.round(matrixStackTotal2 * matrixStackFactor);
         index = i;
      }

      return matrixStackTotal;
   }

   private void renderAmount(DrawContext context, String matrixStackText, float matrixStackValue, float matrixStackBound, boolean matrixStackFlag, float previewFactor) {
      float[] ifAbsent = this.matrixStackFloatArrayMap.computeIfAbsent(matrixStackText, matrixStackText2 -> new float[1]);
      ifAbsent[0] = AmountPreviewUtils.getAmount(ifAbsent[0], matrixStackFlag ? 1.0F : 0.0F, matrixStackFlag ? 16.0F : 11.0F, previewFactor, 0.002F);
      float matrixStackLimit = ifAbsent[0];
      float rootDivisor = MatrixPeekUtilsRender.getAmount();
      if (matrixStackLimit > 0.002F) {
         MatrixPeekUtilsRender.renderMatrixPeek(
            context, this.glfwAmount - 4.0F, matrixStackValue + 1.0F, this.cubeSchematicAmount + 8.0F, 18.0F, 5.0F, MatrixStackUtilsAmount.getValue(-1, 0.04F * matrixStackLimit)
         );
      }

      float matrixStackValue4 = this.glfwAmount + 1.0F;
      handle(matrixStackValue4, MatrixStackUtilsAmount.getAmount(matrixStackValue + 10.0F - 4.0F), matrixStackBound, CardMatrixUtils.getValue(-7630437, -920587, matrixStackBound));
      MatrixStackUtilsRelease.handle(matrixStackText, matrixStackValue4 + 8.0F + 6.0F, matrixStackValue, 20.0F, 0.82F, CardMatrixUtils.getValue(-7630437, -920587, Math.max(matrixStackLimit, matrixStackBound)));
      MatrixPeekUtilsRender.renderMatrixPeek(
         context, this.glfwAmount, MatrixStackUtilsAmount.getAmount(matrixStackValue + 20.0F - rootDivisor), this.cubeSchematicAmount, rootDivisor, CardMatrixUtils.getValueInt()
      );
   }

   private static void handle(float matrixStackValue, float roundOffset, float matrixStackLimit, int matrixStackValue3) {
      if (!(matrixStackLimit <= 0.0F) && !(matrixStackLimit >= 1.0F)) {
         handleTranslatePop(IcoGenUtils.getChevronIco(), matrixStackValue, roundOffset, 90.0F * matrixStackLimit, MatrixStackUtilsAmount.getValue(matrixStackValue3, AmountPreviewUtils.getMin(2.0F - 2.0F * matrixStackLimit)));
         handleTranslatePop(
            IcoGenUtils.getChevrondnIco(), matrixStackValue, roundOffset, 90.0F * (matrixStackLimit - 1.0F), MatrixStackUtilsAmount.getValue(matrixStackValue3, AmountPreviewUtils.getMin(2.0F * matrixStackLimit))
         );
      } else {
         MatrixStackUtilsRelease.handle(matrixStackLimit >= 1.0F ? IcoGenUtils.getChevrondnIco() : IcoGenUtils.getChevronIco(), matrixStackValue, roundOffset, 8.0F, matrixStackValue3);
      }
   }

   private MatrixStackHelper.Row getClassForStringConfigSetting(String classText, ConfigSetting autoEspBase) {
      if (autoEspBase instanceof BooleanSetting booleanBase) {
         return new MatrixStackHelper.Row(classText, new RenderBeta(booleanBase, 0, 0, 0, 20), 20);
      } else if (autoEspBase instanceof ButtonBase buttonBase) {
         return new MatrixStackHelper.Row(classText, new RendererPress(buttonBase, 0, 0, 0, 24), 24);
      } else if (autoEspBase instanceof IntegerSetting integerHelper) {
         return new MatrixStackHelper.Row(classText, new IntegerFlag(integerHelper, 0, 0, 0, 32), 32);
      } else if (autoEspBase instanceof DoubleSetting doubleHelper) {
         return new MatrixStackHelper.Row(classText, new FlagHandle(doubleHelper, 0, 0, 0, 32), 32);
      } else if (autoEspBase instanceof EnumSetting enumHelper) {
         return new MatrixStackHelper.Row(classText, new ScissorDisable(enumHelper, 0, 0, 0, 24), 24);
      } else if (autoEspBase instanceof StringBase stringBase) {
         return new MatrixStackHelper.Row(classText, new MaxRenderer(stringBase, 0, 0, 0, 44), 44);
      } else {
         return autoEspBase instanceof KeybindSetting keybindBase ? new MatrixStackHelper.Row(classText, new ListeningGlfw(keybindBase, 0, 0, 0, 24, this), 24) : null;
      }
   }

   public boolean canGlfwListening(int glfwListeningValue, int modifiers) {
      if (this.listeningGlfw != null) {
         ListeningGlfw listeningGlfw = this.listeningGlfw;
         this.listeningGlfw = null;
         listeningGlfw.handleForInt(glfwListeningValue);
         return true;
      } else {
         for (MatrixStackHelper.Row row : this.cubeRowList) {
            if (row.glfwObject instanceof MaxRenderer maxRenderer && maxRenderer.isFlag()) {
               return maxRenderer.canMaxTime(glfwListeningValue, 0, modifiers);
            }
         }

         return false;
      }
   }

   public boolean canFlag(int matrixStackValue, int rendererValue) {
      for (MatrixStackHelper.Row row : this.cubeRowList) {
         if (row.glfwObject instanceof MaxRenderer maxRenderer && maxRenderer.isFlag()) {
            return maxRenderer.canMaxTime((char)matrixStackValue, rendererValue);
         }
      }

      return false;
   }

   @Override
   public void handleListeningGlfw(ListeningGlfw listeningGlfw) {
      this.listeningGlfw = listeningGlfw;
   }

   public void handleMax(double matrixStackValue) {
      this.targetScroll -= (float)matrixStackValue * 26.0F;
      this.updateMaxAmount();
   }

   private static boolean canFlag(MatrixStackHelper.Row row, double matrixStackValue, double mouseY, int button) {
      Object obj = row.glfwObject;
      if (obj instanceof RenderBeta renderBeta) {
         return renderBeta.canFlag(matrixStackValue, mouseY, button);
      } else if (obj instanceof RendererPress rendererPress) {
         return rendererPress.canTime(matrixStackValue, mouseY, button);
      } else if (obj instanceof IntegerFlag integerFlag) {
         return integerFlag.canFlag(matrixStackValue, mouseY, button);
      } else if (obj instanceof FlagHandle flagHandle) {
         return flagHandle.canFlag(matrixStackValue, mouseY, button);
      } else if (obj instanceof ScissorDisable scissorDisable) {
         return scissorDisable.canAmount(matrixStackValue, mouseY, button);
      } else if (obj instanceof MaxRenderer maxRenderer) {
         return maxRenderer.canRendererFont(matrixStackValue, mouseY, button);
      } else {
         return obj instanceof ListeningGlfw listeningGlfw ? listeningGlfw.canApi(matrixStackValue, mouseY, button) : false;
      }
   }

   private float getAmount(String matrixStackText) {
      QuintScissor quintScissor = this.matrixStackQuintScissorMap.get(matrixStackText);
      if (quintScissor == null) {
         return this.matrixStackBooleanMap.getOrDefault(matrixStackText, false) ? 1.0F : 0.0F;
      } else {
         return AmountPreviewUtils.getMin((float)quintScissor.getAmount());
      }
   }

   private void handleFlag(String matrixStackText) {
      boolean matrixStackFlag = !this.matrixStackBooleanMap.getOrDefault(matrixStackText, false);
      this.matrixStackBooleanMap.put(matrixStackText, matrixStackFlag);
      QuintScissor quintScissor = this.matrixStackQuintScissorMap.get(matrixStackText);
      if (quintScissor != null) {
         quintScissor.handleAmountTime(matrixStackFlag ? 1.0 : 0.0);
      }

      for (MatrixStackHelper.Row row : this.cubeRowList) {
         if (row.sectionTitle.equals(matrixStackText)) {
            row.cubeSchematicActive = false;
         }
      }
   }

   public void updateGlfwListening() {
      this.listeningGlfw = null;
      this.next = null;
      this.handle(null);
   }

   @Override
   public boolean canGlfwListening(ListeningGlfw listeningGlfw) {
      return this.listeningGlfw == listeningGlfw;
   }

   public void handleForDoubleInt(double matrixStackValue, double minmaxValue, int button) {
      if (this.next != null) {
         if (this.next.glfwObject instanceof IntegerFlag integerFlag) {
            integerFlag.handleFlagForDoubleInt(matrixStackValue, minmaxValue, button);
         } else if (this.next.glfwObject instanceof FlagHandle flagHandle) {
            flagHandle.handleFlagForDoubleInt(matrixStackValue, minmaxValue, button);
         }
      }
   }

   private void renderAmount(DrawContext context, MatrixStackHelper.Row row, float matrixStackValue, float matrixStackLimit, int matrixStackValue2, int mouseY) {
      handle(row, (int)this.glfwAmount, Math.round(row.rowY), (int)this.cubeSchematicAmount);
      if (row.rowY + row.rowHeight >= matrixStackValue && row.rowY <= matrixStackLimit) {
         render(context, row, matrixStackValue2, mouseY);
      }
   }

   private static final class Row {
      final Object glfwObject;
      boolean cubeSchematicActive;
      final String sectionTitle;
      final int rowHeight;
      float rowY;

      Row(String key, Object object, int value) {
         this.sectionTitle = key;
         this.glfwObject = object;
         this.rowHeight = value;
      }
   }
}
