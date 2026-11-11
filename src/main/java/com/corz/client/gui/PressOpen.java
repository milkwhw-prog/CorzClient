package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.inventory.DealBoard;
import com.corz.client.inventory.VillagerTrader;
import com.corz.client.render.VertexTextureFont;
import com.corz.client.world.ElseBuilder;
import java.awt.Color;
import java.util.function.Supplier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import com.corz.client.categories.bots.AutoBuilder;
import com.corz.client.categories.donutsmp.SpecialItemBuyer;
import com.corz.client.categories.bots.VillagerTradeBot;
import ai.auth.Module;
import com.corz.client.utils.CardStatusUtilsActive;

public final class PressOpen {
   private String schematicPickedText;
   private int pressStartY;
   private String append;
   private int builderOpenValue;
   private static final String PICK_SCHEMATIC_PLACE = "Pick a schematic, place it, then press Start.";
   private int builderMatrixValue;
   private static final Color GRAY = new Color(-7630437, true);
   private int pressStartValue = -1;
   private final RendererPress pressStartRendererPress;
   private static final String TRADES_MARK_YOUR = "Add trades, mark your chests, then press Start.";
   private final String pressStartText;
   private static final String SHARD_TOOL_DEALS = "Compare /ah shard tool deals and buy the best one.";
   private String pressStartTextPressStart;
   private int pressOpenValue;
   private final AutoBuilder autoBuilder;

   private Integer getBuilderAuto() {
      return this.autoBuilder.isEnabled() && this.autoBuilder.schematicBuilder.getLoadedSchematic() != null ? -8393375 : 0;
   }

   private String formatSchematicPicked() {
      ElseBuilder loadedSchematic = this.autoBuilder.schematicBuilder.getLoadedSchematic();
      String loadedSchematicText = this.autoBuilder.schematicBuilder.getLoadedFileName();
      boolean hasVar = loadedSchematic != null;
      boolean schematicPickedFlag = this.autoBuilder.isEnabled() && hasVar;
      int schematicPickedValue = schematicPickedFlag ? CardStatusUtilsActive.getValue() : -1;
      String append = (hasVar ? loadedSchematicText + "|" + loadedSchematic.orElse : "") + "|" + schematicPickedValue;
      if (!append.equals(this.append)) {
         this.append = append;
         if (!hasVar) {
            this.schematicPickedText = "No schematic picked";
         } else {
            String schematicPickedText = loadedSchematicText != null && !loadedSchematicText.isBlank()
               ? PreviewSchematic.formatTextString(loadedSchematicText)
               : (loadedSchematic.orElse != null && !loadedSchematic.orElse.isBlank() ? loadedSchematic.orElse : "Schematic");
            this.schematicPickedText = schematicPickedFlag ? schematicPickedText + "  \u00b7  " + schematicPickedValue + "%" : schematicPickedText;
         }
      }

      return this.schematicPickedText;
   }

   public boolean canMatrixStack(double matrixStackValue, double mouseY, int button) {
      return this.pressStartRendererPress.canTime(matrixStackValue, mouseY, button);
   }

   private PressOpen(String pressStartText, String pressOpenText, String pressOpenText2, Runnable runnable, Supplier<String> supplier, Supplier<Integer> supplier2) {
      this.schematicPickedText = "";
      this.autoBuilder = null;
      this.pressStartText = pressStartText;
      this.pressStartTextPressStart = pressStartText;
      this.builderOpenValue = 44;
      ButtonBase supplierForSupplier = new ButtonBase(pressOpenText, pressOpenText2, pressOpenText, runnable).getSupplier(supplier).getSupplierForSupplier(supplier2);
      this.pressStartRendererPress = new RendererPress(supplierForSupplier, 0, 0, 0, 24);
   }

   public void handleBuilderMatrix(int value, int editClusterValue, int width, int builderOpenValue) {
      this.builderMatrixValue = value;
      this.pressStartY = editClusterValue;
      this.pressOpenValue = width;
      this.builderOpenValue = builderOpenValue;
      this.pressStartRendererPress.handle(value, editClusterValue + 18, width, 24);
   }

   public static PressOpen getOpenDeal(Module Module) {
      if (Module instanceof AutoBuilder autoBuilder) {
         return new PressOpen(autoBuilder);
      } else if (Module instanceof VillagerTradeBot villagerTradeBot) {
         return new PressOpen(
            "Add trades, mark your chests, then press Start.",
            "Open Trader",
            "Open the Villager Trader",
            () -> VillagerTrader.handleScreenInstance(villagerTradeBot),
            villagerTradeBot::getTradeSummaryText,
            villagerTradeBot::getStatusColor
         );
      } else {
         return Module instanceof SpecialItemBuyer specialItemBuyer
            ? new PressOpen(
               "Compare /ah shard tool deals and buy the best one.",
               "Open Deal Board",
               "Open the deal board",
               () -> DealBoard.handleInstance(specialItemBuyer),
               specialItemBuyer::getBoardStatusText,
               specialItemBuyer::getStatusColor
            )
            : null;
      }
   }

   public void renderPressStart(DrawContext context, int pressStartValue, int mouseY) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      if (this.pressOpenValue != this.pressStartValue) {
         this.pressStartValue = this.pressOpenValue;
         this.pressStartTextPressStart = fontRenderer.formatText(this.pressStartText, (this.pressOpenValue - 8) / 0.82F);
      }

      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      matrixStack.push();
      matrixStack.scale(0.82F, 0.82F, 1.0F);
      fontRenderer.handleForStringFloat(this.pressStartTextPressStart, (this.builderMatrixValue + 4) / 0.82F, (this.pressStartY + 2.0F) / 0.82F, GRAY);
      matrixStack.pop();
      this.pressStartRendererPress.handle(this.builderMatrixValue, this.pressStartY + 18, this.pressOpenValue, 24);
      this.pressStartRendererPress.renderTimeRenderer(context, pressStartValue, mouseY);
   }

   private PressOpen(AutoBuilder autoBuilder) {
      this.schematicPickedText = "";
      this.autoBuilder = autoBuilder;
      this.pressStartText = "Pick a schematic, place it, then press Start.";
      this.pressStartTextPressStart = "Pick a schematic, place it, then press Start.";
      this.builderOpenValue = 44;
      ButtonBase supplier = new ButtonBase("Open Builder", "Open the Auto Builder", "Open Builder", () -> SideNudgeSchematic.handleScreenInstance(autoBuilder))
         .getSupplier(this::formatSchematicPicked)
         .getSupplierForSupplier(this::getBuilderAuto);
      this.pressStartRendererPress = new RendererPress(supplier, 0, 0, 0, 24);
   }
}
