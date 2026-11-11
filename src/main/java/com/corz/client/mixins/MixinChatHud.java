package com.corz.client.mixins;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.corz.client.categories.bots.AutoTpaHere;
import com.corz.client.categories.bots.AutoAuctionFlipper;
import com.corz.client.categories.bots.AutoBuilder;
import com.corz.client.utils.ConfirmScreenHelper;
import com.corz.client.categories.misc.NameProtect;

@Mixin({ChatHud.class})
public class MixinChatHud {
   @ModifyVariable(
      method = {"method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V"},
      at = @At("HEAD"),
      argsOnly = true,
      index = 1
   )
   private Text corz_maskChatName(Text text) {
      if (text != null && AutoBuilder.isLowerCaseAvailable()) {
         AutoBuilder.onChatPurchaseMessage(text.getString());
      }

      if (text != null && AutoAuctionFlipper.hasActiveMessageMatchers()) {
         AutoAuctionFlipper.onChatMessageReceived(text.getString());
      }

      if (text != null && ConfirmScreenHelper.isLowerCase()) {
         ConfirmScreenHelper.setMoneyEnoughText(text.getString());
      }

      if (text != null && AutoTpaHere.isRunning()) {
         AutoTpaHere.recordOverlayMessage(text.getString());
      }

      return NameProtect.getDisplayName(text);
   }
}
