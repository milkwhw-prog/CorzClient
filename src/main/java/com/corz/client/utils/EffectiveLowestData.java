package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.corz.client.utils.EyeStandsData;

public record EffectiveLowestData(
   int trueLowestUnfinishedY, int effectiveWorkLayerY, int lookahead, int playerFeetY, long componentKey, int componentSize, int tick
) {
   public static final EffectiveLowestData INSTANCE = new EffectiveLowestData(Integer.MIN_VALUE, Integer.MIN_VALUE, 3, Integer.MIN_VALUE, 0L, 0, -1);

   public List<Integer> getEffectiveLowest() {
      ArrayList arrayList = new ArrayList();
      if (!this.isEffectiveWork()) {
         return arrayList;
      } else {
         arrayList.add(this.effectiveWorkLayerY);
         if (this.trueLowestUnfinishedY != Integer.MIN_VALUE) {
            for (int i = this.effectiveWorkLayerY - 1; i >= this.trueLowestUnfinishedY && arrayList.size() < 6; i--) {
               arrayList.add(i);
            }
         }

         return arrayList;
      }
   }

   public boolean isEffectiveWork() {
      return this.effectiveWorkLayerY != Integer.MIN_VALUE;
   }

   public EffectiveLowestData getLookaheadEffective(EyeStandsData live) {
      return new EffectiveLowestData(
         this.trueLowestUnfinishedY, this.effectiveWorkLayerY, this.lookahead, live.getFeet(), live.component(), live.reachableStands(), live.tick()
      );
   }

   public boolean isLowestUnfinished() {
      return this.isEffectiveWork() && this.trueLowestUnfinishedY != Integer.MIN_VALUE && this.effectiveWorkLayerY > this.trueLowestUnfinishedY;
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "trueLowest=%d effective=%d layers=%s feetY=%d component=%d/%d",
         this.trueLowestUnfinishedY,
         this.effectiveWorkLayerY,
         this.getEffectiveLowest(),
         this.playerFeetY,
         this.componentKey,
         this.componentSize
      );
   }
}
