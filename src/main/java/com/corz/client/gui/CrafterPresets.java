package com.corz.client.gui;

import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.ModulePresetList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import com.corz.client.utils.DisabledMaskData;

public class CrafterPresets extends Screen implements GuiClickApi {
   private int renameDeleteValue;
   private final int toggleCellValue;
   private int presetToggleValue;
   private int disabledSlotsValue;
   private TextFieldWidget textFieldWidget;
   private final ModulePresetList presetsJson;
   private int presetsValue;
   private String renameEmptyText = "";
   private final int presetsCrafterValue = 14;
   private int mouseClickedValue;
   private final Screen currentScreen;
   private String renameNothingText;
   private int crafterPresetsValue;

   private String getAppend() {
      return "Auto-apply: " + (this.presetsJson.isAutoApply() ? "ON" : "OFF");
   }

   @Override
   public boolean mouseClicked(Click click, boolean mouseClickedFlag) {
      int x = (int)click.x();
      int y = (int)click.y();

      for (int i = 0; i < 9; i++) {
         int mouseClickedValue = this.renameDeleteValue + i % 3 * this.toggleCellValue;
         int mouseClickedValue2 = this.presetToggleValue + i / 3 * this.toggleCellValue;
         if (x >= mouseClickedValue && x < mouseClickedValue + this.toggleCellValue - 2 && y >= mouseClickedValue2 && y < mouseClickedValue2 + this.toggleCellValue - 2) {
            this.disabledSlotsValue ^= 1 << i;
            return true;
         }
      }

      List unmodifiableList = this.presetsJson.getPresetList();

      for (int j = 0; j < unmodifiableList.size() && j < 12; j++) {
         int mouseClickedValue3 = this.mouseClickedValue + j * this.presetsCrafterValue;
         if (x >= this.presetsValue && x <= this.presetsValue + this.crafterPresetsValue && y >= mouseClickedValue3 && y < mouseClickedValue3 + this.presetsCrafterValue) {
            ModulePresetList.Preset preset = (ModulePresetList.Preset)unmodifiableList.get(j);
            this.renameNothingText = preset.name();
            this.disabledSlotsValue = preset.mask();
            this.textFieldWidget.setText(preset.name());
            if (mouseClickedFlag) {
               this.presetsJson.selectPreset(preset.name());
               this.renameEmptyText = "selected '" + preset.name() + "'";
               update();
            }

            return true;
         }
      }

      return super.mouseClicked(click, mouseClickedFlag);
   }

   @Override
   public boolean keyPressed(KeyInput keyInput) {
      if (keyInput.key() == 256) {
         this.close();
         return true;
      } else {
         return super.keyPressed(keyInput);
      }
   }

   private void updateRenameNothing() {
      if (this.renameNothingText == null) {
         this.renameEmptyText = "nothing to rename";
      } else {
         String renameNothingText = this.textFieldWidget.getText();
         if (this.presetsJson.renamePreset(this.renameNothingText, renameNothingText)) {
            this.renameNothingText = ModulePresetList.sanitizePresetName(renameNothingText);
            this.renameEmptyText = "renamed to '" + this.renameNothingText + "'";
            update();
         } else {
            this.renameEmptyText = "rename failed (empty or duplicate)";
         }
      }
   }

   @Override
   public void render(DrawContext context, int crafterPresetsLimit, int crafterPresetsLimit2, float crafterPresetsValue) {
      int min2 = Math.min(400, this.width - 20);
      int min3 = Math.min(230, this.height - 20);
      int x1 = (this.width - min2) / 2;
      int y1 = (this.height - min3) / 2;
      context.fill(0, 0, this.width, this.height, -1610612736);
      context.fill(x1, y1, x1 + min2, y1 + min3, -267119072);
      context.fill(x1, y1, x1 + min2, y1 + 1, 1090519039);
      context.drawText(this.textRenderer, "Crafter Presets  " + this.presetsJson.getSummaryString(), x1 + 10, y1 + 9, -1, false);
      List unmodifiableList = this.presetsJson.getPresetList();
      String presets = this.presetsJson.getSelectedPresetName();

      for (int i = 0; i < unmodifiableList.size() && i < 12; i++) {
         ModulePresetList.Preset preset = (ModulePresetList.Preset)unmodifiableList.get(i);
         int crafterPresetsValue4 = this.mouseClickedValue + i * this.presetsCrafterValue;
         boolean crafterPresetsFlag = crafterPresetsLimit >= this.presetsValue && crafterPresetsLimit <= this.presetsValue + this.crafterPresetsValue && crafterPresetsLimit2 >= crafterPresetsValue4 && crafterPresetsLimit2 < crafterPresetsValue4 + this.presetsCrafterValue;
         boolean equalsIgnoreCase = preset.name().equalsIgnoreCase(this.renameNothingText);
         boolean equalsIgnoreCase2 = preset.name().equalsIgnoreCase(presets);
         if (crafterPresetsFlag || equalsIgnoreCase) {
            context.fill(
               this.presetsValue - 2, crafterPresetsValue4 - 1, this.presetsValue + this.crafterPresetsValue, crafterPresetsValue4 + this.presetsCrafterValue - 1, equalsIgnoreCase ? 1346412799 : 822083583
            );
         }

         context.drawText(this.textRenderer, (equalsIgnoreCase2 ? "\u2605 " : "  ") + preset.name(), this.presetsValue, crafterPresetsValue4 + 2, equalsIgnoreCase2 ? -10406 : -1644826, false);
         context.drawText(this.textRenderer, DisabledMaskData.formatText(preset.mask()), this.presetsValue + this.crafterPresetsValue - 60, crafterPresetsValue4 + 2, -6643540, false);
      }

      if (unmodifiableList.isEmpty()) {
         context.drawText(this.textRenderer, "no presets yet", this.presetsValue, this.mouseClickedValue + 2, -6643540, false);
      }

      for (int j = 0; j < 9; j++) {
         int x12 = this.renameDeleteValue + j % 3 * this.toggleCellValue;
         int y12 = this.presetToggleValue + j / 3 * this.toggleCellValue;
         boolean canFlag = DisabledMaskData.canFlag(this.disabledSlotsValue, j);
         boolean crafterPresetsFlag3 = crafterPresetsLimit >= x12 && crafterPresetsLimit < x12 + this.toggleCellValue - 2 && crafterPresetsLimit2 >= y12 && crafterPresetsLimit2 < y12 + this.toggleCellValue - 2;
         context.fill(x12, y12, x12 + this.toggleCellValue - 2, y12 + this.toggleCellValue - 2, canFlag ? -8376272 : -12957126);
         if (crafterPresetsFlag3) {
            context.fill(x12, y12, x12 + this.toggleCellValue - 2, y12 + this.toggleCellValue - 2, 822083583);
         }

         context.drawText(this.textRenderer, canFlag ? "X" : j + 1 + "", x12 + 7, y12 + 6, -1, false);
      }

      context.drawText(
         this.textRenderer,
         "disabled slots: "
            + DisabledMaskData.formatText(this.disabledSlotsValue)
            + (this.renameNothingText == null ? "  (new)" : "  (" + this.renameNothingText + ")"),
         this.renameDeleteValue + 3 * this.toggleCellValue + 8,
         this.presetToggleValue + 2,
         -5195576,
         false
      );
      context.drawText(
         this.textRenderer, "click a cell to toggle", this.renameDeleteValue + 3 * this.toggleCellValue + 8, this.presetToggleValue + 14, -6643540, false
      );
      context.drawText(this.textRenderer, this.renameEmptyText, x1 + 10, y1 + min3 - 38, -6631169, false);
      super.render(context, crafterPresetsLimit, crafterPresetsLimit2, crafterPresetsValue);
   }

   private static void update() {
      try {
         ModuleConfigAutosave.updateFlag2();
      } catch (Throwable e) {
      }
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   @Override
   protected void init() {
      int crafterPresetsValue = Math.min(400, this.width - 20);
      int min2 = Math.min(230, this.height - 20);
      int crafterPresetsValue3 = (this.width - crafterPresetsValue) / 2;
      int crafterPresetsValue4 = (this.height - min2) / 2;
      this.presetsValue = crafterPresetsValue3 + 10;
      this.mouseClickedValue = crafterPresetsValue4 + 28;
      this.crafterPresetsValue = 150;
      this.renameDeleteValue = crafterPresetsValue3 + 190;
      this.presetToggleValue = crafterPresetsValue4 + 28;
      this.textFieldWidget = new TextFieldWidget(
         this.textRenderer, this.renameDeleteValue, this.presetToggleValue + 3 * this.toggleCellValue + 8, 190, 16, Text.literal("Preset name")
      );
      this.textFieldWidget.setMaxLength(24);
      if (this.renameNothingText != null) {
         this.textFieldWidget.setText(this.renameNothingText);
      }

      this.addDrawableChild(this.textFieldWidget);
      int crafterPresetsValue5 = this.presetToggleValue + 3 * this.toggleCellValue + 30;
      byte crafterPresetsValue6 = 60;
      byte crafterPresetsValue7 = 16;
      this.addDrawableChild(
         ButtonWidget.builder(Text.literal("Save"), buttonWidget -> this.updateRenameLibrary()).dimensions(this.renameDeleteValue, crafterPresetsValue5, crafterPresetsValue6, crafterPresetsValue7).build()
      );
      this.addDrawableChild(
         ButtonWidget.builder(Text.literal("Rename"), buttonWidget2 -> this.updateRenameNothing()).dimensions(this.renameDeleteValue + 65, crafterPresetsValue5, crafterPresetsValue6, crafterPresetsValue7).build()
      );
      this.addDrawableChild(
         ButtonWidget.builder(Text.literal("Delete"), buttonWidget3 -> this.updateRenameNothingRenameNothingText()).dimensions(this.renameDeleteValue + 130, crafterPresetsValue5, crafterPresetsValue6, crafterPresetsValue7).build()
      );
      this.addDrawableChild(
         ButtonWidget.builder(Text.literal("Select"), buttonWidget4 -> this.updateRenamePreset()).dimensions(this.renameDeleteValue, crafterPresetsValue5 + 20, crafterPresetsValue6, crafterPresetsValue7).build()
      );
      this.addDrawableChild(ButtonWidget.builder(Text.literal(this.getAppend()), buttonWidget5 -> {
         this.presetsJson.setAutoApply(!this.presetsJson.isAutoApply());
         buttonWidget5.setMessage(Text.literal(this.getAppend()));
         update();
      }).dimensions(this.renameDeleteValue + 65, crafterPresetsValue5 + 20, 90, crafterPresetsValue7).build());
      this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), buttonWidget6 -> this.close()).dimensions(crafterPresetsValue3 + crafterPresetsValue - 60, crafterPresetsValue4 + min2 - 24, 50, crafterPresetsValue7).build());
      this.addDrawableChild(ButtonWidget.builder(Text.literal("New"), buttonWidget7 -> {
         this.renameNothingText = null;
         this.disabledSlotsValue = 0;
         this.textFieldWidget.setText("");
         this.renameEmptyText = "new preset";
      }).dimensions(this.presetsValue, crafterPresetsValue4 + min2 - 24, 50, crafterPresetsValue7).build());
   }

   public CrafterPresets(Screen currentScreen, ModulePresetList presetsJson, Integer integer) {
      super(Text.literal("Crafter Presets"));
      this.toggleCellValue = 22;
      this.currentScreen = currentScreen;
      this.presetsJson = presetsJson;
      ModulePresetList.Preset presets = presetsJson.getSelectedPreset();
      if (integer != null) {
         this.disabledSlotsValue = integer & 511;
         this.renameNothingText = null;
      } else if (presets != null) {
         this.disabledSlotsValue = presets.mask();
         this.renameNothingText = presets.name();
      }
   }

   private void updateRenameLibrary() {
      String renameLibraryText = this.textFieldWidget.getText();
      if (this.presetsJson.savePreset(renameLibraryText, this.disabledSlotsValue)) {
         this.renameNothingText = ModulePresetList.sanitizePresetName(renameLibraryText);
         this.renameEmptyText = "saved '" + this.renameNothingText + "'";
         update();
      } else {
         this.renameEmptyText = "name empty or library full";
      }
   }

   private void updateRenamePreset() {
      if (this.renameNothingText == null) {
         this.renameEmptyText = "save the preset first";
      } else {
         if (this.presetsJson.selectPreset(this.renameNothingText)) {
            this.renameEmptyText = "selected '" + this.renameNothingText + "'";
            update();
         }
      }
   }

   private void updateRenameNothingRenameNothingText() {
      if (this.renameNothingText == null) {
         this.renameEmptyText = "nothing to delete";
      } else {
         if (this.presetsJson.deletePreset(this.renameNothingText)) {
            this.renameEmptyText = "deleted '" + this.renameNothingText + "'";
            this.renameNothingText = null;
            this.textFieldWidget.setText("");
            this.disabledSlotsValue = 0;
            update();
         }
      }
   }

   @Override
   public void close() {
      MinecraftClient.getInstance().setScreen(this.currentScreen);
   }
}
