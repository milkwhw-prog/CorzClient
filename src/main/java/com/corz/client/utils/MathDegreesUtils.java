package com.corz.client.utils;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;

public final class MathDegreesUtils {
   private MathDegreesUtils() {
   }

   public static float[] getWrapDegrees(float wrapDegreesValue, float wrapDegreesValue2, float yawPitchValue, float f2, float wrapDegreesFactor, float wrapDegreesBound, float pilotStatusBound, float wrapDegreesDivisor) {
      float wrapDegrees = MathHelper.wrapDegrees(yawPitchValue - wrapDegreesValue);
      float wrapDegreesFactor2 = f2 - wrapDegreesValue2;
      float distance = (float)Math.sqrt(wrapDegrees * wrapDegrees + wrapDegreesFactor2 * wrapDegreesFactor2);
      if (distance < 0.001F) {
         return new float[]{wrapDegreesValue, wrapDegreesValue2};
      } else {
         float min3 = Math.min(distance, Math.max(pilotStatusBound, Math.min(wrapDegreesBound, distance * wrapDegreesFactor)));
         float wrapDegreesValue5 = wrapDegrees * min3 / distance;
         float wrapDegreesValue6 = wrapDegreesFactor2 * min3 / distance;
         if (wrapDegreesDivisor > 0.0F) {
            wrapDegreesValue5 = Math.round(wrapDegreesValue5 / wrapDegreesDivisor) * wrapDegreesDivisor;
            wrapDegreesValue6 = Math.round(wrapDegreesValue6 / wrapDegreesDivisor) * wrapDegreesDivisor;
         }

         return new float[]{wrapDegreesValue + wrapDegreesValue5, MathHelper.clamp(wrapDegreesValue2 + wrapDegreesValue6, -90.0F, 90.0F)};
      }
   }

   public static Direction getCosSin(float cosSinValue, float cosSinFactor) {
      float angle = cosSinFactor * (float) (Math.PI / 180.0);
      float angle2 = -cosSinValue * (float) (Math.PI / 180.0);
      float sin = MathHelper.sin(angle);
      float cos = MathHelper.cos(angle);
      float sin2 = MathHelper.sin(angle2);
      float cos2 = MathHelper.cos(angle2);
      boolean cosSinFlag = sin2 > 0.0F;
      boolean cosSinFlag2 = sin < 0.0F;
      boolean cosSinFlag3 = cos2 > 0.0F;
      float cosSinFactor2 = cosSinFlag ? sin2 : -sin2;
      float cosSinLimit = cosSinFlag2 ? -sin : sin;
      float cosSinFactor3 = cosSinFlag3 ? cos2 : -cos2;
      float cosSinValue2 = cosSinFactor2 * cos;
      float cosSinValue3 = cosSinFactor3 * cos;
      Direction direction = cosSinFlag ? Direction.EAST : Direction.WEST;
      Direction direction2 = cosSinFlag2 ? Direction.UP : Direction.DOWN;
      Direction direction3 = cosSinFlag3 ? Direction.SOUTH : Direction.NORTH;
      if (cosSinFactor2 > cosSinFactor3) {
         return cosSinLimit > cosSinValue2 ? direction2 : direction;
      } else {
         return cosSinLimit > cosSinValue3 ? direction2 : direction3;
      }
   }

   public static float[] getDegreesThrow(Direction direction, Direction direction2, float degreesThrowValue, float value, float degreesThrowValue2, float degreesThrowValue3, float degreesThrowValue4) {
      float degreesThrowValue22 = degreesThrowValue;
      float degreesThrowValue5;
      if (direction.getAxis() == Axis.Y) {
         float degreesThrowFactor = direction == Direction.UP ? -1.0F : 1.0F;
         degreesThrowValue5 = degreesThrowFactor * MathHelper.clamp(degreesThrowFactor * value, 51.0F + 5.0F * degreesThrowValue2, 87.0F - 3.0F * degreesThrowValue3);
         if (direction2 != null) {
            degreesThrowValue22 = getWrapDegrees(degreesThrowValue, Direction.getHorizontalDegreesOrThrow(direction2), 33.0F + 5.0F * degreesThrowValue4);
         }
      } else {
         float horizontalDegreesOrThrow = Direction.getHorizontalDegreesOrThrow(direction);
         degreesThrowValue22 = getWrapDegrees(degreesThrowValue, horizontalDegreesOrThrow, 32.0F + 5.0F * degreesThrowValue3);
         float degrees = Math.abs(MathHelper.wrapDegrees(degreesThrowValue22 - horizontalDegreesOrThrow));
         float max = (float)Math.toDegrees(Math.atan(Math.cos(Math.toRadians(degrees)))) - (6.0F + 4.0F * degreesThrowValue4);
         degreesThrowValue5 = MathHelper.clamp(value, -max, max);
      }

      return new float[]{degreesThrowValue22, degreesThrowValue5};
   }

   public static boolean canDegreesHorizontal(Direction direction, Direction direction2, float degreesHorizontalValue, float cosSinFactor) {
      return getCosSin(degreesHorizontalValue, cosSinFactor) != direction ? false : direction2 == null || Direction.fromHorizontalDegrees(degreesHorizontalValue) == direction2;
   }

   static float getWrapDegrees(float wrapDegreesValue, float horizontalDegreesOrThrow, float max) {
      float wrapDegrees = MathHelper.wrapDegrees(wrapDegreesValue - horizontalDegreesOrThrow);
      return wrapDegreesValue + (MathHelper.clamp(wrapDegrees, -max, max) - wrapDegrees);
   }
}
