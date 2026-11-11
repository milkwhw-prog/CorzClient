package com.corz.client.utils;
import com.corz.client.utils.Cells;
import com.corz.client.utils.StandFinder;

public final class AnchorSafety {
   public static final String LAVA_LOCKED = "lava-locked";
   public static final String LAVA = "lava";
   public static final String DEEP = "deep";
   public static final String TRAP = "trap";
   public static final String UNDERWATER = "underwater";
   public static final String PROTECTED = "protected";
   public static final String HIDDEN = "hidden";
   public static final String UNREACHABLE = "unreachable";
   public static final String HARD = "hard";

   public static boolean hasTrapNearby(Cells cells, int anchorSafetyValue, int anchorSafetyValue2, int unpackLongZ) {
      for (int i = -4; i <= 4; i++) {
         for (int j = -4; j <= 4; j++) {
            for (int k = -4; k <= 4; k++) {
               if ((i != 0 || j != 0 || k != 0) && Cells.hasAnyFlag(cells.getCellFlags(anchorSafetyValue + i, anchorSafetyValue2 + j, unpackLongZ + k), 16)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean isNextToLava(Cells cells, int anchorSafetyValue, int i, int unpackLongZ) {
      return Cells.hasAnyFlag(cells.getCellFlags(anchorSafetyValue + 1, i, unpackLongZ), 2)
         || Cells.hasAnyFlag(cells.getCellFlags(anchorSafetyValue - 1, i, unpackLongZ), 2)
         || Cells.hasAnyFlag(cells.getCellFlags(anchorSafetyValue, i + 1, unpackLongZ), 2)
         || Cells.hasAnyFlag(cells.getCellFlags(anchorSafetyValue, i - 1, unpackLongZ), 2)
         || Cells.hasAnyFlag(cells.getCellFlags(anchorSafetyValue, i, unpackLongZ + 1), 2)
         || Cells.hasAnyFlag(cells.getCellFlags(anchorSafetyValue, i, unpackLongZ - 1), 2);
   }

   private AnchorSafety() {
   }

   public static AnchorSafety.Verdict evaluateAnchorSafety(
      Cells cells, int lavaUnreachableValue, int i, int unpackLongZ, int cellsValue, StandFinder.Result result, boolean lavaUnreachableFlag, boolean lavaUnreachableFlag2
   ) {
      if (lavaUnreachableFlag2) {
         return AnchorSafety.Verdict.unsafe("protected");
      } else if (isNextToLava(cells, lavaUnreachableValue, i, unpackLongZ)) {
         return AnchorSafety.Verdict.unsafe("lava");
      } else if (hasTrapNearby(cells, lavaUnreachableValue, i, unpackLongZ)) {
         return AnchorSafety.Verdict.unsafe("trap");
      } else if (cellsValue - i > 32) {
         return AnchorSafety.Verdict.unsafe("deep");
      } else if (result == null) {
         return AnchorSafety.Verdict.unsafe("unreachable");
      } else if (!result.exposed().isEmpty()) {
         return AnchorSafety.Verdict.INSTANCE;
      } else if (lavaUnreachableFlag && !result.dig().isEmpty()) {
         return AnchorSafety.Verdict.INSTANCE;
      } else if (!lavaUnreachableFlag && !result.dig().isEmpty()) {
         return AnchorSafety.Verdict.unsafe("hidden");
      } else if (result.wet() > 0 && result.reachable() <= result.wet() + result.lavaFloor()) {
         return AnchorSafety.Verdict.unsafe("underwater");
      } else if (result.lavaFloor() > 0) {
         return AnchorSafety.Verdict.unsafe("lava-locked");
      } else {
         return result.hard() > 0 ? AnchorSafety.Verdict.unsafe("hard") : AnchorSafety.Verdict.unsafe("unreachable");
      }
   }

   public record Verdict(boolean safe, String reason) {
      public static final AnchorSafety.Verdict INSTANCE = new AnchorSafety.Verdict(true, "ok");

      public static AnchorSafety.Verdict unsafe(String reason) {
         return new AnchorSafety.Verdict(false, reason);
      }
   }
}
