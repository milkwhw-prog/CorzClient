package com.corz.client.world;

import com.corz.client.entity.ImmutablePos;
import net.minecraft.util.math.BlockPos;

public record TickStatusData(
   boolean attached,
   String status,
   ImmutablePos.Kind kind,
   BlockPos base,
   int crystalId,
   double value,
   int targetId,
   boolean holding,
   long lastPlaceTick,
   long lastBreakTick,
   long lastActionTick,
   int wantedSlot,
   String directiveStatus
) {
   public static final TickStatusData INSTANCE = new TickStatusData(
      false, null, null, null, -1, 0.0, -1, false, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, -1, null
   );

   public TickStatusData(
      boolean lastBreakTick,
      String lastActionTick,
      ImmutablePos.Kind kind,
      BlockPos targetId,
      int crystalId,
      double value,
      int lastPlaceTick,
      boolean base,
      long status,
      long attached,
      long wantedSlot,
      int holding
   ) {
      this(lastBreakTick, lastActionTick, kind, targetId, crystalId, value, lastPlaceTick, base, status, attached, wantedSlot, holding, null);
   }
}
