package com.corz.client.world;

import com.corz.client.inventory.BlockEntityUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record ForcedFaceData(
   BlockPos forcedBase,
   BlockPos forcedObsidianCell,
   Direction clickFace,
   boolean hold,
   boolean breakNow,
   boolean trustObsidian,
   BlockEntityUtils.Predicted targetAt,
   BlockPos support,
   Direction supportFace
) {
   public ForcedFaceData(
      BlockPos targetAt,
      BlockPos trustObsidian,
      Direction hold,
      boolean breakNow,
      boolean clickFace,
      boolean forcedObsidianCell,
      BlockEntityUtils.Predicted forcedBase
   ) {
      this(targetAt, trustObsidian, hold, breakNow, clickFace, forcedObsidianCell, forcedBase, null, null);
   }
}
