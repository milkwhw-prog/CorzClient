package com.corz.client.utils;
import com.corz.client.utils.LoftLatheCopy;

public final class ForeEar {
   public final float offsetX;
   public final ForeEar foreEar;
   public final float shinFootAmount;
   public final float foreEarAmount;
   public final int boneIndex;
   public final String foreEarText;
   final LoftLatheCopy rootTransform = new LoftLatheCopy();

   public boolean canFlag(ForeEar foreEar) {
      for (ForeEar cursor = this; cursor != null; cursor = cursor.foreEar) {
         if (cursor == foreEar) {
            return true;
         }
      }

      return false;
   }

   ForeEar(int boneIndex, String text, ForeEar parent, float offsetX, float foreEarAmount, float shinFootAmount) {
      this.boneIndex = boneIndex;
      this.foreEarText = text;
      this.foreEar = parent;
      this.offsetX = offsetX;
      this.foreEarAmount = foreEarAmount;
      this.shinFootAmount = shinFootAmount;
   }
}
