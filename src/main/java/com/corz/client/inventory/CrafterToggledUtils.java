package com.corz.client.inventory;

import com.corz.client.config.ModulePresetList;
import com.corz.client.config.ModulePresetList.Preset;
import com.corz.client.gui.CrafterPresets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.text.Text;
import com.corz.client.categories.bots.AutoBuilder;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.DisabledMaskData;

public final class CrafterToggledUtils {
   private static AutoBuilder getAutoBuilder() {
      try {
         return (AutoBuilder)ModuleRegistry.getModule(AutoBuilder.class);
      } catch (Throwable e) {
         return null;
      }
   }

   public static ModulePresetList getPresetsJson() {
      AutoBuilder autoBuilder = getAutoBuilder();
      return autoBuilder == null ? null : autoBuilder.getCrafterPresets();
   }

   public static int getEnabledSlot(CrafterScreenHandler crafterScreenHandler, int enabledSlotValue) {
      int enabledSlot = 0;

      for (int getValuesItem : DisabledMaskData.getValues(getDisabledSlot(crafterScreenHandler), enabledSlotValue)) {
         boolean enabledSlotFlag = !DisabledMaskData.canFlag(enabledSlotValue, getValuesItem);
         AutoBuilder.handleInteractionChanged(crafterScreenHandler, getValuesItem, enabledSlotFlag);
         crafterScreenHandler.setSlotEnabled(getValuesItem, enabledSlotFlag);
         enabledSlot++;
      }

      return enabledSlot;
   }

   public static void handleToggledCrafter(CrafterScreenHandler crafterScreenHandler) {
      ModulePresetList presetsJson = getPresetsJson();
      if (presetsJson != null) {
         Preset presets = presetsJson.getSelectedPreset();
         MinecraftClient instance = MinecraftClient.getInstance();
         if (presets == null) {
            instance.setScreen(new CrafterPresets(instance.currentScreen, presetsJson, null));
         } else {
            int enabledSlot = getEnabledSlot(crafterScreenHandler, presets.mask());
            if (instance.player != null) {
               instance.player
                  .sendMessage(
                     Text.literal(
                        "\u00a7b[CrafterPresets]\u00a7r loaded '"
                           + presets.name()
                           + "' "
                           + DisabledMaskData.formatText(presets.mask())
                           + " ("
                           + enabledSlot
                           + " slot"
                           + (enabledSlot == 1 ? "" : "s")
                           + " toggled)"
                     ),
                     true
                  );
            }
         }
      }
   }

   public static boolean isFlag() {
      return getPresetsJson() != null;
   }

   public static void handleScreenInstance(CrafterScreenHandler crafterScreenHandler) {
      ModulePresetList presetsJson = getPresetsJson();
      if (presetsJson != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         instance.setScreen(new CrafterPresets(instance.currentScreen, presetsJson, getDisabledSlot(crafterScreenHandler)));
      }
   }

   public static int getDisabledSlot(CrafterScreenHandler crafterScreenHandler) {
      int disabledSlot = 0;

      for (int i = 0; i < 9; i++) {
         if (crafterScreenHandler.isSlotDisabled(i)) {
            disabledSlot |= 1 << i;
         }
      }

      return disabledSlot;
   }

   private CrafterToggledUtils() {
   }
}
