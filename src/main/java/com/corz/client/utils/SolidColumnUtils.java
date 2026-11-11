package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.SolidBlockedUtils;
import com.corz.client.utils.PistonStickyUtils;

public final class SolidColumnUtils {
   private static boolean canSolid(SolidColumnUtils.Cell cell, SolidBlockedUtils.Solid solid) {
      return solid.isSolid(cell.x(), cell.y(), cell.z());
   }

   private static boolean canSolidForCellSolid(SolidColumnUtils.Cell cell, SolidBlockedUtils.Solid solid) {
      for (int i = 0; i < 6; i++) {
         int[] solidValues = PistonStickyUtils.getValues(i);
         if (solid.isSolid(cell.x() + solidValues[0], cell.y() + solidValues[1], cell.z() + solidValues[2])) {
            return true;
         }
      }

      return false;
   }

   private SolidColumnUtils() {
   }

   private static boolean canBlocked(SolidColumnUtils.Cell cell, SolidBlockedUtils.Blocked blocked) {
      return blocked != null && blocked.isBlocked(cell.x(), cell.y(), cell.z());
   }

   public static SolidColumnUtils.Step getColumnSide(
      int columnSideValue, int columnSideValue2, int columnSideValue3, int pistonStickyValue, int columnSideBound, SolidBlockedUtils.Solid solid, SolidBlockedUtils.Blocked blocked
   ) {
      if (PistonStickyUtils.canFlag(pistonStickyValue) && solid != null) {
         int[] columnSideValues = PistonStickyUtils.getValues(pistonStickyValue);
         SolidColumnUtils.Cell cell = new SolidColumnUtils.Cell(columnSideValue + columnSideValues[0], columnSideValue2 + columnSideValues[1], columnSideValue3 + columnSideValues[2]);
         if (!canBlocked(cell, blocked) && !canSolid(cell, solid)) {
            if (canSolidForCellSolid(cell, solid)) {
               return new SolidColumnUtils.Step(cell, cell, cell, SolidColumnUtils.Stage.CONNECTOR, 1);
            } else {
               SolidColumnUtils.Step columnSide = null;
               if (pistonStickyValue == 0) {
                  SolidColumnUtils.Cell cell2 = new SolidColumnUtils.Cell(cell.x(), cell.y() - 1, cell.z());
                  if (!canBlocked(cell2, blocked)) {
                     List solidBlocked = SolidBlockedUtils.getSolidBlocked(cell2.x(), cell2.y(), cell2.z(), Math.max(1, columnSideBound), solid, blocked);
                     if (solidBlocked != null && !solidBlocked.isEmpty()) {
                        long[] columnSideValues2 = (long[])solidBlocked.get(0);
                        columnSide = new SolidColumnUtils.Step(
                           cell,
                           cell2,
                           new SolidColumnUtils.Cell((int)columnSideValues2[0], (int)columnSideValues2[1], (int)columnSideValues2[2]),
                           SolidColumnUtils.Stage.SIDE_COLUMN,
                           solidBlocked.size() + 1
                        );
                     }
                  }
               }

               int[] ints = new int[]{2, 3, 4, 5};

               for (int intsItem : ints) {
                  int[] values3 = PistonStickyUtils.getValues(intsItem);
                  SolidColumnUtils.Cell cell3 = new SolidColumnUtils.Cell(cell.x() + values3[0], cell.y(), cell.z() + values3[2]);
                  if (!canBlocked(cell3, blocked)) {
                     List solidBlocked2 = SolidBlockedUtils.getSolidBlocked(cell3.x(), cell3.y(), cell3.z(), Math.max(1, columnSideBound), solid, blocked);
                     if (solidBlocked2 != null && !solidBlocked2.isEmpty()) {
                        long[] columnSideValues4 = (long[])solidBlocked2.get(0);
                        SolidColumnUtils.Step step = new SolidColumnUtils.Step(
                           cell,
                           cell3,
                           new SolidColumnUtils.Cell((int)columnSideValues4[0], (int)columnSideValues4[1], (int)columnSideValues4[2]),
                           SolidColumnUtils.Stage.SIDE_COLUMN,
                           solidBlocked2.size() + 1
                        );
                        if (columnSide == null || step.estimatedRemaining() < columnSide.estimatedRemaining()) {
                           columnSide = step;
                        }
                     }
                  }
               }

               return columnSide;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public record Cell(int x, int y, int z) {
   }

   public static enum Stage {
      SIDE_COLUMN,
      CONNECTOR;
   }

   public record Step(
      SolidColumnUtils.Cell connector, SolidColumnUtils.Cell column, SolidColumnUtils.Cell next, SolidColumnUtils.Stage stage, int estimatedRemaining
   ) {
   }
}
