package com.corz.client.categories.combat;

import com.corz.client.entity.ClientThreadUtils;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.lwjgl.glfw.GLFW;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;

public class ObsidianOptimizer extends Module {
   private int lastSelectedSlot = -1;

   public static void syncSelectedSlot(int selectedSlot) {
      if (selectedSlot >= 0 && selectedSlot <= 8) {
         if (ModuleRegistry.getModule(ObsidianOptimizer.class) instanceof ObsidianOptimizer obsidianOptimizer) {
            obsidianOptimizer.lastSelectedSlot = selectedSlot;
         }
      }
   }

   public ObsidianOptimizer() {
      super("Obsidian Optimizer", IconsPngType.COMBAT);
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.interactionManager != null && mc.world != null) {
         if (mc.currentScreen == null) {
            int selectedSlot = mc.player.getInventory().getSelectedSlot();
            int lastSelectedSlot2 = this.lastSelectedSlot;
            this.lastSelectedSlot = selectedSlot;
            if (!ClientThreadUtils.isFlag() && !ClientThreadUtils.isFlag6()) {
               if (selectedSlot != lastSelectedSlot2) {
                  if (mc.player.getInventory().getStack(selectedSlot).isOf(Items.OBSIDIAN)) {
                     if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 1) != 1) {
                        if (mc.crosshairTarget instanceof BlockHitResult blockHitResult
                           && blockHitResult.getType() == Type.BLOCK
                           && mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult).isAccepted()) {
                           mc.player.swingHand(Hand.MAIN_HAND);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onEnable() {
      this.lastSelectedSlot = mc.player != null ? mc.player.getInventory().getSelectedSlot() : -1;
   }
}
