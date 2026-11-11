package com.corz.client.utils;
import com.corz.client.utils.RefBareData;

public final class UnplacedAntiUtils {

   public static float getAmount(float unplacedAntiValue, float unplacedAntiValue2, boolean unplacedAntiFlag, int unplacedAntiValue3, float unplacedAntiBound) {
      float unplacedAntiValue4 = getAmount(unplacedAntiValue) + getAmount(unplacedAntiValue2);
      float wrapDegreesBound = Math.max(0.0F, unplacedAntiValue4 - (unplacedAntiFlag ? 1.0F : 4.0F));
      if (unplacedAntiValue3 <= 10) {
         wrapDegreesBound = Math.min(wrapDegreesBound, 6.0F);
      }

      if (!Float.isNaN(unplacedAntiBound)) {
         wrapDegreesBound = Math.min(wrapDegreesBound, unplacedAntiBound);
      }

      return Math.max(0.0F, wrapDegreesBound);
   }

   private UnplacedAntiUtils() {
   }

   public static float getBareRef(float bareRefValue, float unplacedAntiBound, boolean bareRefFlag, int bareRefValue3, float bareRefValue4, RefBareData refBareData) {
      if (refBareData != null && refBareData.ref()) {
         float bareRefValue5 = getAmount(bareRefValue) + getAmount(unplacedAntiBound);
         return refBareData.bare() ? Math.max(0.0F, bareRefValue5 - 2.0F) : Float.MAX_VALUE;
      } else {
         int bareRefValue6 = refBareData != null && refBareData.on() ? Integer.MAX_VALUE : bareRefValue3;
         return getAmount(bareRefValue, unplacedAntiBound, bareRefFlag, bareRefValue6, bareRefValue4);
      }
   }

   private static float getAmount(float unplacedAntiBound) {
      return Float.isFinite(unplacedAntiBound) ? Math.max(0.0F, unplacedAntiBound) : 0.0F;
   }
}
