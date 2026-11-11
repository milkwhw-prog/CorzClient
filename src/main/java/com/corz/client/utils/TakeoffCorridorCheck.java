package com.corz.client.utils;

import java.util.function.LongPredicate;

public final class TakeoffCorridorCheck {
   public static TakeoffCorridorCheck.Proof checkTakeoffAndCorridor(long blockPosValue, long proofSupportValue, LongPredicate longPredicate, LongPredicate longPredicate2) {
      int valueInt = ProofSupportUtils.getValueInt(blockPosValue);
      int builderAutoValue = ProofSupportUtils.getValue(blockPosValue);
      int valueForLong = ProofSupportUtils.getValueForLong(blockPosValue);
      int valueInt6 = ProofSupportUtils.getValueInt(proofSupportValue);
      int valueForLong7 = ProofSupportUtils.getValueForLong(proofSupportValue);
      int blockPosValue8 = valueInt + Integer.signum(valueInt6 - valueInt);
      int blockPosValue9 = valueForLong + Integer.signum(valueForLong7 - valueForLong);
      boolean blockPosFlag = longPredicate.test(ProofSupportUtils.getTime(valueInt, builderAutoValue + 1, valueForLong)) && longPredicate.test(ProofSupportUtils.getTime(valueInt, builderAutoValue + 2, valueForLong));
      boolean blockPosFlag2 = false;

      for (int i = 1; i <= 2; i++) {
         if (longPredicate2.test(ProofSupportUtils.getTime(blockPosValue8, builderAutoValue - i, blockPosValue9))) {
            blockPosFlag2 = true;
            break;
         }
      }

      boolean blockPosFlag3 = blockPosFlag2 && !longPredicate2.test(ProofSupportUtils.getTime(blockPosValue8, builderAutoValue, blockPosValue9));

      for (int j = 0; j <= 2; j++) {
         blockPosFlag3 &= longPredicate.test(ProofSupportUtils.getTime(blockPosValue8, builderAutoValue + j, blockPosValue9)) && longPredicate.test(ProofSupportUtils.getTime(valueInt6, builderAutoValue + j, valueForLong7));
      }

      return new TakeoffCorridorCheck.Proof(blockPosFlag, blockPosFlag3);
   }

   private TakeoffCorridorCheck() {
   }

   public record Proof(boolean takeoffClear, boolean corridorClear) {
   }
}
