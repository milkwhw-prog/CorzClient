package com.corz.client.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue.Custom;
import net.minecraft.client.util.math.MatrixStack.Entry;
import org.joml.Matrix4f;
import org.joml.Vector3f;

final class PositionMatrixCustom implements Custom {
   private VertexConsumer vertexConsumer;
   private static final float[] positionMatrixTable = new float[97];
   private float renderAlpha = 1.0F;
   private int segmentCount;
   private float positionMatrixAmount;
   private int positionMatrixValue;
   private final PositionMatrixCustom.Sink positionMatrixSink;
   private Matrix4f positionMatrix;
   private static final float[] table = new float[97];
   private static final float[] SEGMENT_ANGLES = new float[97];
   private int fillColor;
   private int shadowColor;
   private int edgeColor;
   private final Vector3f vectorVertexVector3f = new Vector3f();

   static int getValue(int positionMatrixValue, float positionMatrixValue2, float positionMatrixValue3, float matrixRotateLimit, float positionMatrixValue5) {
      float positionMatrixValue6 = positionMatrixValue2 + (positionMatrixValue3 - positionMatrixValue2) * MatrixRotateUtilsHandle.getAmountFloat(matrixRotateLimit) + 0.1F * MatrixRotateUtilsHandle.getAmountFloat(positionMatrixValue5);
      return getValueForIntFloat(positionMatrixValue, positionMatrixValue6);
   }

   PositionMatrixCustom() {
      this.positionMatrixSink = this::handleVectorVertex;
   }

   static void handle(PositionMatrixCustom.Sink sink, int positionMatrixValue, int positionMatrixValue2, int positionMatrixValue3, int positionMatrixValue4, float matrixRotateLimit, int positionMatrixValue6, float positionMatrixValue7) {
      float positionMatrixLimit = MatrixRotateUtilsHandle.getAmountFloat(matrixRotateLimit);
      if (positionMatrixLimit > 0.0F) {
         if (positionMatrixValue >>> 24 != 0) {
            handleAmounts(sink, 0.58F, getValueInt(positionMatrixValue, positionMatrixLimit));
         }

         if (positionMatrixValue4 >>> 24 != 0) {
            handleAmounts(sink, 0.42F, getValueInt(positionMatrixValue4, positionMatrixLimit));
         }

         if (positionMatrixValue2 >>> 24 != 0) {
            handleAmounts(sink, 0.58F, 0.0F, 0.01F, positionMatrixValue2, positionMatrixLimit);
         }

         if (positionMatrixValue3 >>> 24 != 0) {
            handleAmounts(sink, 0.72F, 0.0F, 0.007F, positionMatrixValue3, positionMatrixLimit);
         }
      }

      if (positionMatrixValue6 >>> 24 != 0) {
         handleAmounts(sink, 0.52F, positionMatrixValue7, 0.012F, positionMatrixValue6, 1.0F);
      }
   }

   private static void handle(PositionMatrixCustom.Sink sink, float positionMatrixValue, float positionMatrixValue2, float sinkValue, float positionMatrixValue4, float positionMatrixValue5, float positionMatrixValue6, int positionMatrixValue7, int positionMatrixValue8) {
      sink.v(positionMatrixValue, positionMatrixValue5, positionMatrixValue2, positionMatrixValue7);
      sink.v(sinkValue, positionMatrixValue5, positionMatrixValue4, positionMatrixValue7);
      sink.v(sinkValue, positionMatrixValue6, positionMatrixValue4, positionMatrixValue8);
      sink.v(positionMatrixValue, positionMatrixValue6, positionMatrixValue2, positionMatrixValue8);
   }

   void handleAmount(int value, int crystalsValue, int value3, int value4, float amount, int value5, float amount2) {
      this.fillColor = value;
      this.edgeColor = crystalsValue;
      this.shadowColor = value3;
      this.segmentCount = value4;
      this.renderAlpha = amount;
      this.positionMatrixValue = value5;
      this.positionMatrixAmount = amount2;
   }

   static void handleAmounts(PositionMatrixCustom.Sink sink, float positionMatrixFactor, float positionMatrixValue, float positionMatrixValue2, int positionMatrixValue3, float positionMatrixLimit) {
      int positionMatrixValue4 = positionMatrixValue3 & 16777215;
      float positionMatrixValue5 = positionMatrixValue + positionMatrixValue2;
      if (!(positionMatrixLimit >= 1.0F)) {
         float angle = positionMatrixLimit * (float) Math.PI;
         float positionMatrixValue6 = (float)Math.sin(angle) * positionMatrixFactor;
         float positionMatrixValue7 = (float)Math.cos(angle) * positionMatrixFactor;

         for (int i = 0; i < 96; i++) {
            if (!(SEGMENT_ANGLES[i + 1] <= -angle) && !(SEGMENT_ANGLES[i] >= angle)) {
               float positionMatrixValue8 = SEGMENT_ANGLES[i] < -angle ? -positionMatrixValue6 : positionMatrixTable[i] * positionMatrixFactor;
               float positionMatrixValue9 = SEGMENT_ANGLES[i] < -angle ? positionMatrixValue7 : table[i] * positionMatrixFactor;
               float sinkValue = SEGMENT_ANGLES[i + 1] > angle ? positionMatrixValue6 : positionMatrixTable[i + 1] * positionMatrixFactor;
               float positionMatrixValue11 = SEGMENT_ANGLES[i + 1] > angle ? positionMatrixValue7 : table[i + 1] * positionMatrixFactor;
               handle(sink, positionMatrixValue8, positionMatrixValue9, sinkValue, positionMatrixValue11, positionMatrixValue, positionMatrixValue5, positionMatrixValue3, positionMatrixValue4);
            }
         }
      } else {
         for (int j = 0; j < 96; j++) {
            handle(sink, positionMatrixTable[j] * positionMatrixFactor, table[j] * positionMatrixFactor, positionMatrixTable[j + 1] * positionMatrixFactor, table[j + 1] * positionMatrixFactor, positionMatrixValue, positionMatrixValue5, positionMatrixValue3, positionMatrixValue4);
         }
      }
   }

   static void handle(PositionMatrixCustom.Sink sink, int positionMatrixValue, int positionMatrixValue2, int positionMatrixValue3, float matrixRotateLimit, int positionMatrixValue5, float positionMatrixValue6) {
      handle(sink, 0, positionMatrixValue, positionMatrixValue2, positionMatrixValue3, matrixRotateLimit, positionMatrixValue5, positionMatrixValue6);
   }

   static int getValue(int positionMatrixValue, float matrixRotateLimit) {
      return getValueForIntFloat(positionMatrixValue, 0.05F + 0.09F * MatrixRotateUtilsHandle.getAmountFloat(matrixRotateLimit));
   }

   static int getValueForIntFloat(int positionMatrixValue, float matrixRotateLimit) {
      int round = Math.round(MatrixRotateUtilsHandle.getAmountFloat(matrixRotateLimit) * 255.0F);
      return round << 24 | positionMatrixValue & 16777215;
   }

   static int getValueInt(int positionMatrixValue, float positionMatrixFactor) {
      return getValueForIntFloat(positionMatrixValue, (positionMatrixValue >>> 24) / 255.0F * positionMatrixFactor);
   }

   @Override
   public void render(Entry entry, VertexConsumer vertexConsumer) {
      this.positionMatrix = entry.getPositionMatrix();
      this.vertexConsumer = vertexConsumer;

      try {
         handle(this.positionMatrixSink, this.fillColor, this.edgeColor, this.shadowColor, this.segmentCount, this.renderAlpha, this.positionMatrixValue, this.positionMatrixAmount);
      } finally {
         this.positionMatrix = null;
         this.vertexConsumer = null;
      }
   }

   static void handleAmounts(PositionMatrixCustom.Sink sink, float positionMatrixFactor, int positionMatrixValue) {
      int positionMatrixValue2 = positionMatrixValue & 16777215;

      for (int i = 0; i < 96; i++) {
         sink.v(0.0F, 0.0F, 0.0F, positionMatrixValue);
         sink.v(0.0F, 0.0F, 0.0F, positionMatrixValue);
         sink.v(table[i + 1] * positionMatrixFactor, 0.0F, positionMatrixTable[i + 1] * positionMatrixFactor, positionMatrixValue2);
         sink.v(table[i] * positionMatrixFactor, 0.0F, positionMatrixTable[i] * positionMatrixFactor, positionMatrixValue2);
      }
   }

   static {
      for (int i = 0; i <= 96; i++) {
         double angle = -Math.PI + i * (Math.PI * 2) / 96.0;
         SEGMENT_ANGLES[i] = (float)angle;
         positionMatrixTable[i] = (float)Math.sin(angle);
         table[i] = (float)Math.cos(angle);
      }
   }

   private void handleVectorVertex(float vectorVertexValue, float vectorVertexValue2, float vectorVertexValue3, int vectorVertexValue4) {
      this.positionMatrix.transformPosition(vectorVertexValue, vectorVertexValue2, vectorVertexValue3, this.vectorVertexVector3f);
      this.vertexConsumer.vertex(this.vectorVertexVector3f.x, this.vectorVertexVector3f.y, this.vectorVertexVector3f.z).color(vectorVertexValue4);
   }

   interface Sink {
      void v(float sinkValue, float sinkValue2, float sinkValue3, int positionMatrixValue);
   }
}
