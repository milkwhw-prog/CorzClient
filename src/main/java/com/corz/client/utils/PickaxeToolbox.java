package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import com.corz.client.utils.BlockPosApi;
import com.corz.client.utils.BlockBreakMath;

public final class PickaxeToolbox {
   private final List<PickaxeToolbox.Tool> pickaxes;
   private final boolean underwater;
   public static final PickaxeToolbox.Tool NO_TOOL = new PickaxeToolbox.Tool(-1, 1.0F, 0, false, false);
   public static final PickaxeToolbox EMPTY = new PickaxeToolbox(List.of(), -1, -1, false);
   private final int fatigueAmplifier;
   private final int hasteAmplifier;

   public PickaxeToolbox(List<PickaxeToolbox.Tool> list, int value, int value2, boolean flag) {
      ArrayList arrayList = new ArrayList();
      if (list != null) {
         for (PickaxeToolbox.Tool tool : list) {
            if (tool != null && tool.pickaxe()) {
               arrayList.add(tool);
            }
         }
      }

      this.pickaxes = List.copyOf(arrayList);
      this.hasteAmplifier = value;
      this.fatigueAmplifier = value2;
      this.underwater = flag;
   }

   public int getBreakTicks(PickaxeToolbox.Tool tool, BlockPosApi blockPosApi, BlockPos pos, boolean suitableMultiplierFlag) {
      if (blockPosApi != null && pos != null) {
         float hardnessWorld = blockPosApi.getHardnessWorld(pos);
         return hardnessWorld < 0.0F
            ? Integer.MAX_VALUE
            : BlockBreakMath.getTicksUntilBroken(this.getBreakProgressPerTick(tool, hardnessWorld, blockPosApi.isToolRequired(pos), blockPosApi.getToolNeeds(pos), suitableMultiplierFlag));
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public PickaxeToolbox.Tool getToolInSlot(int suitableMultiplierValue) {
      for (PickaxeToolbox.Tool next : this.pickaxes) {
         if (next.slot() == suitableMultiplierValue) {
            return next;
         }
      }

      return null;
   }

   public float getBreakProgressPerTick(PickaxeToolbox.Tool tool, float suitableMultiplierValue, boolean suitableMultiplierFlag, int pickaxeValue, boolean suitableMultiplierFlag2) {
      PickaxeToolbox.Tool tool2 = tool == null ? NO_TOOL : tool;
      float immutablePosLimit = isPickaxeUseful(tool2, pickaxeValue) ? tool2.speed() : 1.0F;
      int immutablePosValue = isPickaxeUseful(tool2, pickaxeValue) ? tool2.efficiency() : 0;
      float blockBreakSpeed = BlockBreakMath.getBlockBreakSpeed(immutablePosLimit, immutablePosValue, this.hasteAmplifier, this.fatigueAmplifier, this.underwater, suitableMultiplierFlag2);
      return BlockBreakMath.getBreakProgressPerTick(blockBreakSpeed, suitableMultiplierValue, canHarvestWith(tool2, suitableMultiplierFlag, pickaxeValue));
   }

   public static boolean canHarvestWith(PickaxeToolbox.Tool tool, boolean suitablePickaxeFlag, int suitablePickaxeValue) {
      if (!suitablePickaxeFlag) {
         return true;
      } else if (tool == null || !tool.pickaxe()) {
         return false;
      } else {
         return suitablePickaxeValue == 1 ? true : suitablePickaxeValue == 2 && tool.suitableObsidian();
      }
   }

   public List<PickaxeToolbox.Tool> getPickaxes() {
      return this.pickaxes;
   }

   public static boolean isPickaxeUseful(PickaxeToolbox.Tool tool, int pickaxeValue) {
      return tool != null && tool.pickaxe() && pickaxeValue != 0;
   }

   public PickaxeToolbox.Tool getFastestTool() {
      PickaxeToolbox.Tool next = null;
      int suitableMultiplierLimit = Integer.MAX_VALUE;

      for (PickaxeToolbox.Tool tool : this.pickaxes) {
         int suitableMultiplierValue = BlockBreakMath.getObsidianBreakTicks(tool.speed(), tool.efficiency(), tool.suitableObsidian());
         if (next == null || suitableMultiplierValue < suitableMultiplierLimit || suitableMultiplierValue == suitableMultiplierLimit && tool.isHotbarSlot() && !next.isHotbarSlot()) {
            next = tool;
            suitableMultiplierLimit = suitableMultiplierValue;
         }
      }

      return next;
   }

   public static PickaxeToolbox ofTools(PickaxeToolbox.Tool... tools) {
      return new PickaxeToolbox(tools == null ? List.of() : List.of(tools), -1, -1, false);
   }

   public boolean hasPickaxes() {
      return !this.pickaxes.isEmpty();
   }

   public PickaxeToolbox.Pick findBestPick(BlockPosApi blockPosApi, BlockPos pos, boolean suitableMultiplierFlag, boolean suitableMultiplierFlag2) {
      if (blockPosApi != null && pos != null && !(blockPosApi.getHardnessWorld(pos) < 0.0F)) {
         int suitableMultiplierValue = this.getBreakTicks(NO_TOOL, blockPosApi, pos, suitableMultiplierFlag2);
         PickaxeToolbox.Pick pick = new PickaxeToolbox.Pick(-1, suitableMultiplierValue, false);

         for (PickaxeToolbox.Tool tool : this.pickaxes) {
            if (!suitableMultiplierFlag || tool.isHotbarSlot()) {
               int breakTicks = this.getBreakTicks(tool, blockPosApi, pos, suitableMultiplierFlag2);
               if (breakTicks < pick.ticks()) {
                  pick = new PickaxeToolbox.Pick(tool.slot(), breakTicks, true);
               }
            }
         }

         return pick;
      } else {
         return PickaxeToolbox.Pick.INSTANCE;
      }
   }

   public boolean hasHotbarPickaxe() {
      for (PickaxeToolbox.Tool tool : this.pickaxes) {
         if (tool.isHotbarSlot()) {
            return true;
         }
      }

      return false;
   }

   public record Pick(int slot, int ticks, boolean pickaxe) {
      public static final PickaxeToolbox.Pick INSTANCE = new PickaxeToolbox.Pick(-1, Integer.MAX_VALUE, false);

      public boolean isHotbarSlot() {
         return this.slot >= 0 && this.slot < 9;
      }

      public boolean isBareHand() {
         return this.slot == -1;
      }
   }

   public record Tool(int slot, float speed, int efficiency, boolean pickaxe, boolean suitableObsidian) {
      public boolean isHotbarSlot() {
         return this.slot >= 0 && this.slot < 9;
      }
   }
}
