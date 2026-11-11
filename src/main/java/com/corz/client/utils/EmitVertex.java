package com.corz.client.utils;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue.Custom;
import net.minecraft.client.util.math.MatrixStack.Entry;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.corz.client.utils.SubmitRadians;
import com.corz.client.utils.RadiansAge;
import com.corz.client.utils.AmountsEmit;
import com.corz.client.utils.MatrixSubmitUtils;
import com.corz.client.utils.SubmitEmitUtils;

public final class EmitVertex {
   final SubmitRadians submitCustomSubmitRadians;
   public final RadiansAge radiansAge = new RadiansAge();
   boolean captureCapeActive;
   final AmountsEmit vectorOutlineAmountsEmit;
   private final Vector3f vectorOutlineVector3f;
   private final Vector3f transformedNormal;
   int vertexColor;
   final Custom custom;
   final float[] captureAmounts = new float[14];
   final float[] captureTable = new float[14];
   int emitValue;
   int emitValue2SubmitCustom;
   final Custom submitCustomCustom;

   private void handleVectorOutline(Entry entry, VertexConsumer vertices) {
      try {
         Matrix4f positionMatrix = entry.getPositionMatrix();
         float[] vectorOutlineValues = this.vectorOutlineAmountsEmit.vectorOutlineTable;
         int vectorOutlineLimit = this.vectorOutlineAmountsEmit.getValue() & -4;
         int vectorOutlineCounter = 0;

         for (int i = 0; vectorOutlineCounter < vectorOutlineLimit; i += 8) {
            positionMatrix.transformPosition(vectorOutlineValues[i], vectorOutlineValues[i + 1], vectorOutlineValues[i + 2], this.vectorOutlineVector3f);
            vertices.vertex(this.vectorOutlineVector3f.x, this.vectorOutlineVector3f.y, this.vectorOutlineVector3f.z).color(this.vertexColor).texture(vectorOutlineValues[i + 3], vectorOutlineValues[i + 4]);
            vectorOutlineCounter++;
         }
      } catch (Throwable e) {
         MatrixSubmitUtils.handleFlag("outline", e);
      }
   }

   private void handleVectorEmit(Entry entry, VertexConsumer vertices) {
      try {
         Matrix4f positionMatrix = entry.getPositionMatrix();
         float[] vectorEmitValues = this.vectorOutlineAmountsEmit.vectorOutlineTable;
         int vectorEmitLimit = this.vectorOutlineAmountsEmit.getValue() & -4;
         int vectorEmitCounter = 0;

         for (int i = 0; vectorEmitCounter < vectorEmitLimit; i += 8) {
            positionMatrix.transformPosition(vectorEmitValues[i], vectorEmitValues[i + 1], vectorEmitValues[i + 2], this.vectorOutlineVector3f);
            entry.transformNormal(vectorEmitValues[i + 5], vectorEmitValues[i + 6], vectorEmitValues[i + 7], this.transformedNormal);
            vertices.vertex(
               this.vectorOutlineVector3f.x,
               this.vectorOutlineVector3f.y,
               this.vectorOutlineVector3f.z,
               -1,
               vectorEmitValues[i + 3],
               vectorEmitValues[i + 4],
               this.emitValue2SubmitCustom,
               this.emitValue,
               this.transformedNormal.x,
               this.transformedNormal.y,
               this.transformedNormal.z
            );
            vectorEmitCounter++;
         }
      } catch (Throwable e) {
         MatrixSubmitUtils.handleFlag("emit", e);
      }
   }

   public EmitVertex() {
      this.submitCustomSubmitRadians = new SubmitRadians();
      this.vectorOutlineAmountsEmit = new AmountsEmit();
      this.vectorOutlineVector3f = new Vector3f();
      this.transformedNormal = new Vector3f();
      this.custom = this::handleVectorEmit;
      this.submitCustomCustom = this::handleVectorOutline;
   }

   void updateCaptureAmounts() {
      if (!this.captureCapeActive) {
         SubmitEmitUtils.handleForFloatArraySubmitRadians(this.captureAmounts, this.captureTable, this.submitCustomSubmitRadians);
      }

      this.vectorOutlineAmountsEmit.handleAmounts(this.captureAmounts, this.captureTable);
   }
}
