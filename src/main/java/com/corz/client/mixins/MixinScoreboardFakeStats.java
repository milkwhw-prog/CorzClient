package com.corz.client.mixins;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.donutsmp.FakeStats;
import ai.auth.ModuleRegistry;

@Mixin({InGameHud.class})
public class MixinScoreboardFakeStats {
   private static final String SIDEBAR = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V";

   @Inject(
      method = {"method_1757(Lnet/minecraft/class_332;Lnet/minecraft/class_266;)V"},
      at = {@At("HEAD")}
   )
   private void corz_beginSidebar(DrawContext drawContext, ScoreboardObjective scoreboardObjective, CallbackInfo callbackInfo) {
      FakeStats fs = fakeStats();
      if (fs != null) {
         fs.resetLineCounters();
      }
   }

   @Redirect(
      method = {"method_1757(Lnet/minecraft/class_332;Lnet/minecraft/class_266;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_327;method_27525(Lnet/minecraft/class_5348;)I",
         ordinal = 1
      ),
      require = 0
   )
   private int corz_lineWidth(TextRenderer textRenderer, StringVisitable stringVisitable) {
      FakeStats fs = fakeStats();
      if (fs != null && stringVisitable instanceof Text t) {
         try {
            return textRenderer.getWidth(fs.rewriteLineFirstPass(t));
         } catch (Exception e) {
         }
      }

      return textRenderer.getWidth(stringVisitable);
   }

   @ModifyArg(
      method = {"method_1757(Lnet/minecraft/class_332;Lnet/minecraft/class_266;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_332;method_51439(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IIIZ)V",
         ordinal = 1
      ),
      index = 1,
      require = 0
   )
   private Text corz_lineText(Text corz_lineText) {
      FakeStats fs = fakeStats();
      if (fs != null) {
         try {
            return fs.rewriteLineSecondPass(corz_lineText);
         } catch (Exception e) {
         }
      }

      return corz_lineText;
   }

   private static FakeStats fakeStats() {
      FakeStats fs = (FakeStats)ModuleRegistry.getModule(FakeStats.class);
      return fs != null && fs.isEnabled() ? fs : null;
   }
}
