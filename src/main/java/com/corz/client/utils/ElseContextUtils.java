package com.corz.client.utils;

import com.corz.client.world.ElseBuilder;
import net.minecraft.block.BlockState;
import com.corz.client.utils.Time;

public final class ElseContextUtils {
   private ElseContextUtils() {
   }

   public static ElseContextUtils.Result getEmit(ElseContextUtils.RegionWalk regionWalk) {
      Time time = new Time();
      if (regionWalk != null) {
         regionWalk.emit(time::mixHashSample);
      }

      return new ElseContextUtils.Result(time.getHashDigest(), time.getEventCount());
   }

   public static ElseContextUtils.Result digestElseRegion(ElseBuilder elseBuilder) {
      return elseBuilder == null ? new ElseContextUtils.Result(0L, 0) : getEmit(sink -> {
         for (ElseBuilder.Region region : elseBuilder.elseRegionList) {
            int elseRegionEnd = region.getElseRegionEnd();
            int elseOrElse2Else = region.getElseOrElse2Else();
            int else5 = region.getElse();

            for (int i = 0; i < region.elseArrayValue; i++) {
               for (int j = 0; j < region.regionValue; j++) {
                  for (int k = 0; k < region.elseValue; k++) {
                     BlockState statesBlock = region.getStatesBlock(k, i, j);
                     if (statesBlock != null && !statesBlock.isAir()) {
                        sink.cell(elseRegionEnd + k, elseOrElse2Else + i, else5 + j, statesBlock.toString().hashCode());
                     }
                  }
               }
            }
         }
      });
   }

   public interface CellSink {
      void cell(int cellValue, int cellValue2, int cellValue3, int cellValue4);
   }

   public interface RegionWalk {
      void emit(ElseContextUtils.CellSink cellSink);
   }

   public record Result(long digest, int contentCells) {
   }
}
