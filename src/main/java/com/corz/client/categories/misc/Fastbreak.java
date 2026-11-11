package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.mixins.AccessorClientPlayerInteractionManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class Fastbreak extends Module {
   private final BooleanSetting booleanBase;
   private int togglePaceValue;
   private final BooleanSetting autoPace;
   private final BooleanSetting noBreakDelay;
   private int togglePaceCount;
   private int paceToggleCount;
   private final List<Fastbreak.Pending> paceItems;
   private final DoubleSetting doubleHelper = new DoubleSetting(
      "Speed",
      "Break-speed multiplier. 1.0 = legit speed (Grim-safe). Above 1.0 breaks faster than vanilla, which Grim-style anticheats flag as FastBreak (and strict servers ghost).",
      1.0,
      1.0,
      10.0,
      0.1
   );
   private int breakCooldownTicks;
   private final IntegerSetting integerHelper;

   public void handlePaceToggle(BlockPos pos2, BlockState state) {
      if (this.booleanBase.isEnabled()) {
         this.togglePaceValue = this.togglePaceCount + this.integerHelper.getValue();
      }

      if (this.autoPace.isEnabled() && this.paceItems.size() < 256) {
         this.paceItems.add(new Fastbreak.Pending(pos2, state, this.togglePaceCount));
      }
   }

   @Override
   protected void onEnable() {
      this.breakCooldownTicks = 3;
      this.paceToggleCount = 0;
      this.togglePaceValue = 0;
      this.paceItems.clear();
   }

   public boolean isBase() {
      return this.booleanBase.isEnabled() || this.autoPace.isEnabled();
   }

   private void handlePaceToggle(AccessorClientPlayerInteractionManager accessorClientPlayerInteract) {
      if (mc.world != null) {
         var iterator = this.paceItems.iterator();

         while (iterator.hasNext()) {
            Fastbreak.Pending pending = (Fastbreak.Pending)iterator.next();
            BlockState blockState = mc.world.getBlockState(pending.immutable);
            int paceToggleLimit = this.togglePaceCount - pending.pendingToggleCount;
            boolean paceToggleFlag = blockState.equals(pending.blockState) || !blockState.isAir() && blockState.getBlock() == pending.blockState.getBlock();
            if (paceToggleFlag) {
               this.paceToggleCount = 0;
               int paceToggleValue = this.breakCooldownTicks;
               this.breakCooldownTicks = Math.min(this.breakCooldownTicks + 2, 8);
               iterator.remove();
            } else if (paceToggleLimit >= 10) {
               this.paceToggleCount++;
               if (this.paceToggleCount >= 8 && this.breakCooldownTicks > 0) {
                  int breakCooldownTicks2 = this.breakCooldownTicks--;
                  this.paceToggleCount = 0;
               }

               iterator.remove();
            }
         }
      }

      if (accessorClientPlayerInteract.getBlockBreakingCooldown() > this.breakCooldownTicks) {
         accessorClientPlayerInteract.setBlockBreakingCooldown(this.breakCooldownTicks);
      }
   }

   public Fastbreak() {
      super("Fastbreak", IconsPngType.MISC);
      this.noBreakDelay = new BooleanSetting(
         "No Break Delay", "Remove the client-side pause between consecutive blocks (per-block break time is unchanged). Grim-safe.", true
      );
      this.booleanBase = new BooleanSetting(
         "Auto-Toggle",
         "Automate the 'toggle the module off/on exactly when a block breaks' trick \u2014 fires frame-perfectly on the break tick. Blips the effect off for Toggle Off-Ticks then back on. Takes priority over Auto-Pace. Run with Fastbreak Probe on to see if the window is real (KEPT) or not (GHOST).",
         false
      );
      this.integerHelper = new IntegerSetting(
         "Toggle Off-Ticks", "How many ticks Auto-Toggle holds the effect OFF at each break before switching it back on.", 1, 1, 20
      );
      this.autoPace = new BooleanSetting(
         "Auto-Pace",
         "Self-tune the break rate to the fastest this server accepts with (near-)zero ghosting. Ignored while Auto-Toggle is on. NOTE: converges to your legit pickaxe speed \u2014 clean, not faster.",
         false
      );
      this.togglePaceValue = 0;
      this.breakCooldownTicks = 3;
      this.paceToggleCount = 0;
      this.togglePaceCount = 0;
      this.paceItems = new ArrayList<>();
      this.addSettings(new ConfigSetting[]{this.doubleHelper, this.noBreakDelay, this.booleanBase, this.integerHelper, this.autoPace});
   }

   @Override
   protected void onReset() {
      if (mc.interactionManager != null) {
         this.togglePaceCount++;
         AccessorClientPlayerInteractionManager interactionManager = (AccessorClientPlayerInteractionManager)mc.interactionManager;
         if (this.booleanBase.isEnabled()) {
            if (this.noBreakDelay.isEnabled() && this.isPaceToggle()) {
               interactionManager.setBlockBreakingCooldown(0);
            }
         } else if (this.autoPace.isEnabled()) {
            this.handlePaceToggle(interactionManager);
         } else if (this.noBreakDelay.isEnabled()) {
            interactionManager.setBlockBreakingCooldown(0);
         }
      }
   }

   public boolean isPaceToggle() {
      return !this.booleanBase.isEnabled() || this.togglePaceCount >= this.togglePaceValue;
   }

   @Override
   protected void onDisable() {
      this.paceItems.clear();
   }

   public float getAmount() {
      return (float)this.doubleHelper.getValue();
   }

   private static final class Pending {
      final BlockPos immutable;
      final int pendingToggleCount;
      final BlockState blockState;

      Pending(BlockPos immutable, BlockState blockState, int value) {
         this.immutable = immutable;
         this.blockState = blockState;
         this.pendingToggleCount = value;
      }
   }
}
