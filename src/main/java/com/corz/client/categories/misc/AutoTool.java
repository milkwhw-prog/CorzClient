package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import com.corz.client.utils.ToolBreakerUtils;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class AutoTool extends Module {
   private int selectedSlot;
   private final BooleanSetting booleanBase = new BooleanSetting("Swap Back", "Return to your previous slot when you stop mining", true);

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         BlockPos blockPos = this.getBlockPos();
         if (blockPos == null) {
            this.updateSelectedSlot();
         } else {
            BlockState blockState = mc.world.getBlockState(blockPos);
            if (blockState.isAir()) {
               this.updateSelectedSlot();
            } else {
               PlayerInventory inventory = mc.player.getInventory();
               int selectedSlot = inventory.getSelectedSlot();
               float[] floats = new float[PlayerInventory.getHotbarSize()];
               boolean[] booleans = new boolean[floats.length];

               for (int i = 0; i < floats.length; i++) {
                  ItemStack stack = inventory.getStack(i);
                  floats[i] = stack.getMiningSpeedMultiplier(blockState);
                  booleans[i] = stack.isSuitableFor(blockState);
               }

               int selectBestToolIndex = ToolBreakerUtils.selectBestToolIndex(floats, booleans, selectedSlot);
               if (selectBestToolIndex != selectedSlot) {
                  if (this.selectedSlot < 0) {
                     this.selectedSlot = selectedSlot;
                  }

                  inventory.setSelectedSlot(selectBestToolIndex);
               }
            }
         }
      }
   }

   public AutoTool() {
      super("AutoTool", IconsPngType.MISC);
      this.selectedSlot = -1;
      this.addSettings(new ConfigSetting[]{this.booleanBase});
   }

   private void updateSelectedSlot() {
      if (!this.booleanBase.isEnabled()) {
         this.selectedSlot = -1;
      } else if (this.selectedSlot >= 0 && mc.player != null) {
         if (this.selectedSlot >= 0 && this.selectedSlot < PlayerInventory.getHotbarSize()) {
            mc.player.getInventory().setSelectedSlot(this.selectedSlot);
         }

         this.selectedSlot = -1;
      }
   }

   private BlockPos getBlockPos() {
      if (!mc.options.attackKey.isPressed()) {
         return null;
      } else if (mc.crosshairTarget instanceof BlockHitResult blockHitResult) {
         return blockHitResult.getType() != Type.BLOCK ? null : blockHitResult.getBlockPos();
      } else {
         return null;
      }
   }

   @Override
   protected void onDisable() {
      this.updateSelectedSlot();
   }
}
