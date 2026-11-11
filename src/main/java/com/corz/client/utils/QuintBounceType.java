package com.corz.client.utils;

import java.util.function.DoubleUnaryOperator;

public enum QuintBounceType {
   LINEAR(quintBounceValue -> quintBounceValue),
   SINE_IN(quintBounceFactor -> 1.0 - Math.cos(quintBounceFactor * Math.PI / 2.0)),
   SINE_OUT(quintBounceFactor -> Math.sin(quintBounceFactor * Math.PI / 2.0)),
   SINE_IN_OUT(quintBounceFactor -> -(Math.cos(Math.PI * quintBounceFactor) - 1.0) / 2.0),
   CUBIC_IN(quintBounceFactor -> quintBounceFactor * quintBounceFactor * quintBounceFactor),
   CUBIC_OUT(quintBounceValue -> 1.0 - Math.pow(1.0 - quintBounceValue, 3.0)),
   CUBIC_IN_OUT(quintBounceFactor -> quintBounceFactor < 0.5 ? 4.0 * quintBounceFactor * quintBounceFactor * quintBounceFactor : 1.0 - Math.pow(-2.0 * quintBounceFactor + 2.0, 3.0) / 2.0),
   QUAD_IN(quintBounceFactor -> quintBounceFactor * quintBounceFactor),
   QUAD_OUT(quintBounceValue -> 1.0 - (1.0 - quintBounceValue) * (1.0 - quintBounceValue)),
   QUAD_IN_OUT(quintBounceFactor -> quintBounceFactor < 0.5 ? 2.0 * quintBounceFactor * quintBounceFactor : 1.0 - Math.pow(-2.0 * quintBounceFactor + 2.0, 2.0) / 2.0),
   QUART_IN(quintBounceFactor -> quintBounceFactor * quintBounceFactor * quintBounceFactor * quintBounceFactor),
   QUART_OUT(quintBounceValue -> 1.0 - Math.pow(1.0 - quintBounceValue, 4.0)),
   QUART_IN_OUT(quintBounceFactor -> quintBounceFactor < 0.5 ? 8.0 * quintBounceFactor * quintBounceFactor * quintBounceFactor * quintBounceFactor : 1.0 - Math.pow(-2.0 * quintBounceFactor + 2.0, 4.0) / 2.0),
   QUINT_IN(quintBounceFactor -> quintBounceFactor * quintBounceFactor * quintBounceFactor * quintBounceFactor * quintBounceFactor),
   QUINT_OUT(quintBounceValue -> 1.0 - Math.pow(1.0 - quintBounceValue, 5.0)),
   QUINT_IN_OUT(quintBounceFactor -> quintBounceFactor < 0.5 ? 16.0 * quintBounceFactor * quintBounceFactor * quintBounceFactor * quintBounceFactor * quintBounceFactor : 1.0 - Math.pow(-2.0 * quintBounceFactor + 2.0, 5.0) / 2.0),
   CIRC_IN(quintBounceFactor -> 1.0 - Math.sqrt(1.0 - quintBounceFactor * quintBounceFactor)),
   CIRC_OUT(quintBounceValue -> Math.sqrt(1.0 - Math.pow(quintBounceValue - 1.0, 2.0))),
   CIRC_IN_OUT(quintBounceFactor -> quintBounceFactor < 0.5 ? (1.0 - Math.sqrt(1.0 - 4.0 * quintBounceFactor * quintBounceFactor)) / 2.0 : (Math.sqrt(1.0 - Math.pow(-2.0 * quintBounceFactor + 2.0, 2.0)) + 1.0) / 2.0),
   EXPO_IN(quintBounceValue -> quintBounceValue == 0.0 ? 0.0 : Math.pow(2.0, 10.0 * quintBounceValue - 10.0)),
   EXPO_OUT(quintBounceFactor -> quintBounceFactor == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * quintBounceFactor)),
   EXPO_IN_OUT(quintBounceFactor -> {
      if (quintBounceFactor == 0.0) {
         return 0.0;
      } else if (quintBounceFactor == 1.0) {
         return 1.0;
      } else {
         return quintBounceFactor < 0.5 ? Math.pow(2.0, 20.0 * quintBounceFactor - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * quintBounceFactor + 10.0)) / 2.0;
      }
   }),
   ELASTIC_IN(quintBounceLimit -> {
      if (quintBounceLimit <= 0.0) {
         return 0.0;
      } else if (quintBounceLimit >= 1.0) {
         return 1.0;
      } else {
         double quintBounceFactor = Math.PI * 2.0 / 3.0;
         return -Math.pow(2.0, 10.0 * quintBounceLimit - 10.0) * Math.sin((quintBounceLimit * 10.0 - 0.75) * quintBounceFactor);
      }
   }),
   ELASTIC_OUT(quintBounceLimit -> {
      if (quintBounceLimit <= 0.0) {
         return 0.0;
      } else if (quintBounceLimit >= 1.0) {
         return 1.0;
      } else {
         double quintBounceFactor = Math.PI * 2.0 / 3.0;
         return Math.pow(2.0, -10.0 * quintBounceLimit) * Math.sin((quintBounceLimit * 10.0 - 0.75) * quintBounceFactor) + 1.0;
      }
   }),
   ELASTIC_IN_OUT(
      quintBounceLimit -> {
         if (quintBounceLimit <= 0.0) {
            return 0.0;
         } else if (quintBounceLimit >= 1.0) {
            return 1.0;
         } else {
            double quintBounceFactor = Math.PI * 4.0 / 9.0;
            return quintBounceLimit < 0.5
               ? -(Math.pow(2.0, 20.0 * quintBounceLimit - 10.0) * Math.sin((20.0 * quintBounceLimit - 11.125) * quintBounceFactor)) / 2.0
               : Math.pow(2.0, -20.0 * quintBounceLimit + 10.0) * Math.sin((20.0 * quintBounceLimit - 11.125) * quintBounceFactor) / 2.0 + 1.0;
         }
      }
   ),
   BACK_IN(quintBounceFactor -> {
      double quintBounceFactor2 = 1.70158;
      return (quintBounceFactor2 + 1.0) * quintBounceFactor * quintBounceFactor * quintBounceFactor - quintBounceFactor2 * quintBounceFactor * quintBounceFactor;
   }),
   BACK_OUT(quintBounceValue -> {
      double quintBounceFactor = 1.70158;
      double quintBounceFactor2 = quintBounceValue - 1.0;
      return 1.0 + (quintBounceFactor + 1.0) * quintBounceFactor2 * quintBounceFactor2 * quintBounceFactor2 + quintBounceFactor * quintBounceFactor2 * quintBounceFactor2;
   }),
   BACK_IN_OUT(
      quintBounceFactor -> {
         double quintBounceValue = 2.5949095;
         return quintBounceFactor < 0.5
            ? Math.pow(2.0 * quintBounceFactor, 2.0) * ((quintBounceValue + 1.0) * 2.0 * quintBounceFactor - quintBounceValue) / 2.0
            : (Math.pow(2.0 * quintBounceFactor - 2.0, 2.0) * ((quintBounceValue + 1.0) * (2.0 * quintBounceFactor - 2.0) + quintBounceValue) + 2.0) / 2.0;
      }
   ),
   BOUNCE_IN(quintBounceValue -> 1.0 - getAmountForDouble(1.0 - quintBounceValue)),
   BOUNCE_OUT(QuintBounceType::getAmountForDouble),
   BOUNCE_IN_OUT(quintBounceValue -> quintBounceValue < 0.5 ? (1.0 - getAmountForDouble(1.0 - 2.0 * quintBounceValue)) / 2.0 : (1.0 + getAmountForDouble(2.0 * quintBounceValue - 1.0)) / 2.0),
   EASE_OUT_QUINT(quintBounceValue -> 1.0 - Math.pow(1.0 - quintBounceValue, 5.0)),
   EASE_IN_OUT_BACK(
      quintBounceFactor -> {
         double quintBounceValue = 1.70158;
         double quintBounceValue2 = quintBounceValue * 1.525;
         return quintBounceFactor < 0.5
            ? Math.pow(2.0 * quintBounceFactor, 2.0) * ((quintBounceValue2 + 1.0) * 2.0 * quintBounceFactor - quintBounceValue2) / 2.0
            : (Math.pow(2.0 * quintBounceFactor - 2.0, 2.0) * ((quintBounceValue2 + 1.0) * (2.0 * quintBounceFactor - 2.0) + quintBounceValue2) + 2.0) / 2.0;
      }
   ),
   STEP_START(quintBounceLimit -> quintBounceLimit >= 1.0 ? 1.0 : 0.0),
   STEP_END(quintBounceLimit -> quintBounceLimit > 0.0 ? 1.0 : 0.0),
   STEPS(4) {
      @Override
      public double getUnaryOperator(double unaryOperatorFactor) {
         byte unaryOperatorFactor2 = 4;
         return Math.floor(unaryOperatorFactor * unaryOperatorFactor2) / unaryOperatorFactor2;
      }
   };
   private final DoubleUnaryOperator doubleUnaryOperator;

   private static double getAmountForDouble(double quintBounceFactor) {
      double quintBounceFactor2 = 7.5625;
      double quintBounceDivisor = 2.75;
      if (quintBounceFactor < 1.0 / quintBounceDivisor) {
         return quintBounceFactor2 * quintBounceFactor * quintBounceFactor;
      } else if (quintBounceFactor < 2.0 / quintBounceDivisor) {
         double quintBounceFactor3;
         return quintBounceFactor2 * (quintBounceFactor3 = quintBounceFactor - 1.5 / quintBounceDivisor) * quintBounceFactor3 + 0.75;
      } else {
         double quintBounceFactor4;
         double quintBounceFactor5;
         return quintBounceFactor < 2.5 / quintBounceDivisor ? quintBounceFactor2 * (quintBounceFactor4 = quintBounceFactor - 2.25 / quintBounceDivisor) * quintBounceFactor4 + 0.9375 : quintBounceFactor2 * (quintBounceFactor5 = quintBounceFactor - 2.625 / quintBounceDivisor) * quintBounceFactor5 + 0.984375;
      }
   }

   private QuintBounceType(DoubleUnaryOperator doubleUnaryOperator2) {
      this.doubleUnaryOperator = doubleUnaryOperator2;
   }

   public double getUnaryOperator(double unaryOperatorBound) {
      double unaryOperatorValue = Math.min(1.0, Math.max(0.0, unaryOperatorBound));
      return this.doubleUnaryOperator.applyAsDouble(unaryOperatorValue);
   }

   private QuintBounceType(int quintBounceFactor) {
      this(stepsx -> Math.floor(stepsx * quintBounceFactor) / quintBounceFactor);
   }
}
