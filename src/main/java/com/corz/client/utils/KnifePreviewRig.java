package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntToDoubleFunction;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import com.corz.client.categories.visuals.SwordInspect;
import com.corz.client.utils.KnifeRig;
import com.corz.client.utils.KnifeMesh;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeFinishTextures;
import com.corz.client.utils.KnifeInspectClip;

public final class KnifePreviewRig {
   private static final Map<KnifeRig, KnifePreviewRig.Frame> FRAME_CACHE = new HashMap<>();
   private static final float[] channelsMultiplyTable = new float[3];
   private static final KnifePreviewRig.Channels CHANNELS = new KnifePreviewRig.Channels();

   static KnifePreviewRig.Frame getFrame(KnifeRig knifeRig, String frameText) {
      if (knifeRig != null && KnifeMesh.canModel(knifeRig)) {
         KnifePreviewRig.Frame frame = FRAME_CACHE.get(knifeRig);
         if (frame != null) {
            return frame;
         } else {
            KnifePreviewRig.Frame modelAmounts = getModelAmounts(knifeRig, SwordInspect.getAnticipationSpins(frameText));
            FRAME_CACHE.put(knifeRig, modelAmounts);
            return modelAmounts;
         }
      } else {
         return null;
      }
   }

   public static boolean canFlag(String knifePreviewText, KnifeFinish.Finish finish) {
      if (finish != null && !finish.isCompactFlag()) {
         KnifeRig bayonetStiletto = KnifeRig.getBayonetStiletto(knifePreviewText);
         return bayonetStiletto == null || KnifeFinishTextures.getTimeRigs(bayonetStiletto, finish) != null;
      } else {
         return true;
      }
   }

   static KnifePreviewRig.Frame getInspectClip(KnifeRig knifeRig, float[][] floats, KnifeInspectClip knifeInspectClip) {
      float[] pivotParentValues = new float[3];
      IntToDoubleFunction intToDoubleFunction = inspectClipValue -> 0.0;
      float inspectClipBound = Float.MAX_VALUE;
      float maxValue = Float.MAX_VALUE;
      float wrapDegreesBound = Float.MAX_VALUE;
      float maxValue4 = -Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;
      float maxValue6 = -Float.MAX_VALUE;

      for (int i = 0; i < floats.length; i++) {
         knifeRig.handlePivotParent(i, intToDoubleFunction, pivotParentValues);
         float cos = (float)Math.cos(Math.toRadians(pivotParentValues[0]));
         float sin = (float)Math.sin(Math.toRadians(pivotParentValues[0]));
         float[] inspectClipValues = floats[i];

         for (int j = 0; j < inspectClipValues.length; j += 3) {
            float inspectClipBound7 = cos * inspectClipValues[j] - sin * inspectClipValues[j + 1] + pivotParentValues[1];
            float inspectClipBound8 = sin * inspectClipValues[j] + cos * inspectClipValues[j + 1] + pivotParentValues[2];
            float hashHuntBound = inspectClipValues[j + 2];
            inspectClipBound = Math.min(inspectClipBound, inspectClipBound7);
            maxValue4 = Math.max(maxValue4, inspectClipBound7);
            maxValue = Math.min(maxValue, inspectClipBound8);
            pilotStatusBound = Math.max(pilotStatusBound, inspectClipBound8);
            wrapDegreesBound = Math.min(wrapDegreesBound, hashHuntBound);
            maxValue6 = Math.max(maxValue6, hashHuntBound);
         }
      }

      float inspectClipValue2 = (inspectClipBound + maxValue4) / 2.0F;
      float inspectClipValue3 = (maxValue + pilotStatusBound) / 2.0F;
      float inspectClipValue4 = (wrapDegreesBound + maxValue6) / 2.0F;
      float max10 = 0.0F;
      KnifePreviewRig.Channels channels = new KnifePreviewRig.Channels();
      channels.knifeInspectClip = knifeInspectClip;

      for (int k = 0; k <= (knifeInspectClip == null ? 0 : 120); k++) {
         channels.clipProgress = k / 120.0F;

         for (int m = 0; m < floats.length; m++) {
            knifeRig.handlePivotParent(m, channels, pivotParentValues);
            float cos2 = (float)Math.cos(Math.toRadians(pivotParentValues[0]));
            float sin2 = (float)Math.sin(Math.toRadians(pivotParentValues[0]));
            float[] inspectClipValues2 = floats[m];

            for (int p = 0; p < inspectClipValues2.length; p += 3) {
               float inspectClipFactor = cos2 * inspectClipValues2[p] - sin2 * inspectClipValues2[p + 1] + pivotParentValues[1] - inspectClipValue2;
               float inspectClipFactor2 = sin2 * inspectClipValues2[p] + cos2 * inspectClipValues2[p + 1] + pivotParentValues[2] - inspectClipValue3;
               float inspectClipFactor3 = inspectClipValues2[p + 2] - inspectClipValue4;
               max10 = Math.max(max10, inspectClipFactor * inspectClipFactor + inspectClipFactor2 * inspectClipFactor2 + inspectClipFactor3 * inspectClipFactor3);
            }
         }
      }

      return new KnifePreviewRig.Frame(inspectClipValue2, inspectClipValue3, inspectClipValue4, Math.max(0.001F, (float)Math.sqrt(max10)));
   }

   public static boolean canChannelsMultiply(
      MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, String channelsMultiplyText, KnifeFinish.Finish finish, KnifeInspectClip knifeInspectClip, float channelsMultiplyBound, boolean channelsMultiplyFlag, int channelsMultiplyValue
   ) {
      KnifeRig bayonetStiletto = KnifeRig.getBayonetStiletto(channelsMultiplyText);
      KnifePreviewRig.Frame frame = getFrame(bayonetStiletto, channelsMultiplyText);
      if (frame == null) {
         return false;
      } else {
         CHANNELS.knifeInspectClip = knifeInspectClip;
         CHANNELS.clipProgress = Math.max(0.0F, Math.min(1.0F, channelsMultiplyBound));
         KnifeFinishTextures.Skin timeRigs = KnifeFinishTextures.getTimeRigs(bayonetStiletto, finish);
         matrixStack.push();

         try {
            if (channelsMultiplyFlag && knifeInspectClip != null) {
               matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)CHANNELS.applyAsDouble(11)));
               matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)CHANNELS.applyAsDouble(13)));
               matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(-CHANNELS.applyAsDouble(12))));
            }

            matrixStack.translate(-frame.cx(), -frame.cy(), -frame.cz());

            for (int i = 0; i < bayonetStiletto.getParentParts(); i++) {
               KnifeMesh texturesKnife = KnifeMesh.getTexturesKnife(bayonetStiletto.getParentParts(i).model());
               if (texturesKnife != null) {
                  bayonetStiletto.handlePivotParent(i, CHANNELS, channelsMultiplyTable);
                  matrixStack.push();

                  try {
                     matrixStack.translate(channelsMultiplyTable[1], channelsMultiplyTable[2], 0.0F);
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(channelsMultiplyTable[0]));
                     KnifeFinish.Skin skin2 = timeRigs != null ? timeRigs.getSkins(i) : null;
                     if (skin2 != null && texturesKnife.canAmounts(skin2)) {
                        orderedRenderCommandQueue.submitCustom(
                           matrixStack, timeRigs.renderLayerCorzrenderKnife, (lightx, entry) -> texturesKnife.handleAmountsLight(lightx, entry, channelsMultiplyValue, OverlayTexture.DEFAULT_UV, skin2)
                        );
                        if (timeRigs.canLayerRender(i)) {
                           orderedRenderCommandQueue.getBatchingQueue(1).submitCustom(matrixStack, timeRigs.renderLayer, (skin, vertices) -> texturesKnife.handleAmounts(skin, vertices, skin2));
                        }
                     } else {
                        orderedRenderCommandQueue.submitCustom(
                           matrixStack, KnifeMesh.getCullCutout(), (entry, vertices2) -> texturesKnife.handleAmountsLight(entry, vertices2, channelsMultiplyValue, OverlayTexture.DEFAULT_UV)
                        );
                     }
                  } finally {
                     matrixStack.pop();
                  }
               }
            }
         } finally {
            matrixStack.pop();
         }

         return true;
      }
   }

   public static boolean canInstance(String knifePreviewText) {
      KnifeRig bayonetStiletto = KnifeRig.getBayonetStiletto(knifePreviewText);
      return bayonetStiletto != null && bayonetStiletto != KnifeRig.BUTTERFLY_TP && KnifeMesh.canModel(bayonetStiletto);
   }

   static KnifePreviewRig.Frame getModelAmounts(KnifeRig knifeRig, KnifeInspectClip knifeInspectClip) {
      float[][] floats = new float[knifeRig.getParentParts()][];

      for (int i = 0; i < knifeRig.getParentParts(); i++) {
         floats[i] = KnifeMesh.getTexturesKnife(knifeRig.getParentParts(i).model()).modelTable;
      }

      return getInspectClip(knifeRig, floats, knifeInspectClip);
   }

   private KnifePreviewRig() {
   }

   public static float getRadius(String radiusText) {
      KnifePreviewRig.Frame frame = getFrame(KnifeRig.getBayonetStiletto(radiusText), radiusText);
      return frame == null ? 8.0F : frame.radius();
   }

   static final class Channels implements IntToDoubleFunction {
      float clipProgress;
      KnifeInspectClip knifeInspectClip;

      @Override
      public double applyAsDouble(int doubleValue) {
         return this.knifeInspectClip == null ? 0.0 : this.knifeInspectClip.getTracks(doubleValue, this.clipProgress);
      }
   }

   record Frame(float cx, float cy, float cz, float radius) {
   }
}
