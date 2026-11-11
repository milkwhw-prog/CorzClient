package com.corz.client.mixins;

import com.corz.client.inventory.CrafterToggledUtils;
import net.minecraft.client.gui.screen.ingame.CrafterScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CrafterScreen.class})
public abstract class MixinCrafterScreen extends HandledScreen<CrafterScreenHandler> {
   protected MixinCrafterScreen(CrafterScreenHandler crafterScreenHandler, PlayerInventory playerInventory, Text text) {
      super(crafterScreenHandler, playerInventory, text);
   }

   @Inject(
      method = {"method_25426()V"},
      at = {@At("TAIL")}
   )
   private void corz$addPresetButtons(CallbackInfo callbackInfo) {
      if (CrafterToggledUtils.isFlag()) {
         int bx = this.x + this.backgroundWidth + 4;
         int by = this.y + 4;
         this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Save"), buttonWidget -> CrafterToggledUtils.handleScreenInstance(this.handler)).dimensions(bx, by, 44, 18).build()
         );
         this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Load"), buttonWidget2 -> CrafterToggledUtils.handleToggledCrafter(this.handler)).dimensions(bx, by + 21, 44, 18).build()
         );
      }
   }
}
