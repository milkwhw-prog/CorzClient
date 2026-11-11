package com.corz.client.utils;

import com.corz.client.CorzClient;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorPlaneUtils;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import com.corz.client.utils.PolylinePathMath;
import com.corz.client.categories.visuals.NameTags;
import com.corz.client.categories.visuals.ShowHUD;

public final class PilotRender {
   private final float[] charWidthCache;
   private final double[] pathSampleBuffer;
   private static final double[] BEAM_HEIGHT_FRACTIONS = new double[]{0.0, 0.015, 0.06, 0.18, 0.4, 0.7, 1.0};
   private boolean hudRenderFailed;
   private final int[] beamGlowColors;
   private final double[] pathCumulativeLengths;
   private float markerScreenY;
   private float distanceTextWidth;
   private float markerScreenX;
   private float worldFadeProgress;
   private float resolutionScale;
   private float framebufferHeight;
   private Color distanceTextColor;
   private static final Color OFF_WHITE = new Color(-920587, true);
   private int accentTintColor;
   private float destinationTextWidth;
   private String etaText;
   private final int[] pathOutlineColors;
   private String destinationText;
   private double cameraZ;
   private long lastEtaSeconds;
   private String lastSceneLabel;
   private final double[] pulseRingVertices;
   private long lastNanoTime;
   private final int[] pathFillColors;
   private float panelFadeProgress;
   private float etaTextWidth;
   private double cameraX;
   private final double[] beamPoints;
   private float textVerticalOffset;
   private final int[] viewportRect;
   private float dotSeparatorWidth;
   private float markerFadeProgress;
   private final double[] pathDistanceFromStart;
   private Matrix4f worldMatrix;
   private double windowScreenX;
   private float lineWidthScale;
   private final int[] beamTipColors;
   private final Vector3f projectedPoint;
   private Camera camera;
   private MatrixStack matrixStack;
   private float digitWidth;
   private final int[] ringColors;
   private static final Color GRAY = new Color(-7630437, true);
   private int lastTextColorArgb;
   private final Vector4f viewSpacePoint;
   private final double[] destinationCurvePoints = new double[192];
   private final int[] beamColors;
   private final int[] pathCoreColors;
   private float markerAngleRadians;
   private double cameraY;
   private VertexTextureFont fontRenderer;
   private long lastHudNanoTime;
   private final double[] ringVertices;
   private boolean worldRenderFailed;
   private double windowDepth;
   private int accentColor;
   private final double[] pathRawPoints = new double[1740];
   private double maxRenderDistance;
   private final Matrix4f projectionMatrix;
   private float spareRenderOffset;
   private final Vector3f cameraRelativeVec;
   private static final String[] CHAR_STRINGS = new String[256];
   private static final String DOT_SEPARATOR = "\u00b7";
   private final double[] beamTipPoints;
   private final double[] pathDistanceToEnd;
   private long lastDistanceKey;
   private final double[] pathPoints;
   private static final float[] GLOW_FADE_STEPS = new float[]{1.0F, 0.96F, 0.85F, 0.62F, 0.34F, 0.12F, 0.0F};
   private String distanceText;
   private double windowScreenY;

   private static float clampForFloat(float pilotRenderLimit) {
      return pilotRenderLimit < 0.0F ? 0.0F : (pilotRenderLimit > 1.0F ? 1.0F : pilotRenderLimit);
   }

   private static double clampDouble(double pilotRenderLimit, double pilotRenderLimit2, double pilotRenderValue) {
      return pilotRenderLimit < pilotRenderLimit2 ? pilotRenderLimit2 : (pilotRenderLimit > pilotRenderValue ? pilotRenderValue : pilotRenderLimit);
   }

   private float getTextBaselineY(float pilotRenderValue, float pilotRenderValue2, float pilotRenderFactor) {
      return snapToPixelGrid(pilotRenderValue + pilotRenderValue2 / 2.0F + this.textVerticalOffset * pilotRenderFactor);
   }

   private static void drawProgressRing(float pilotRenderValue, float pilotRenderValue2, float pilotRenderValue3, float pilotRenderValue4, int pilotRenderValue5) {
      float pilotRenderDivisor = MatrixPeekUtilsRender.getAmount();
      float strokeWidth = Math.max(2.0F, (float)Math.round(1.5F * pilotRenderValue3 / 8.0F / pilotRenderDivisor)) * pilotRenderDivisor;
      float handleValue = pilotRenderValue3 / 2.0F - strokeWidth / 2.0F;
      float snapToPixelGrid8 = snapToPixelGrid(pilotRenderValue);
      float snapToPixelGrid9 = snapToPixelGrid(pilotRenderValue2);
      AmountHandle amountHandle = new AmountHandle(snapToPixelGrid8, snapToPixelGrid9, 654311423, false, false, true, false);
      amountHandle.handle(handleValue, strokeWidth);
      amountHandle.updateAmountPanel();
      float pilotRenderLimit = clampForFloat(pilotRenderValue4);
      if (!(pilotRenderLimit <= 0.003F)) {
         AmountHandle amountHandle2 = new AmountHandle(snapToPixelGrid8, snapToPixelGrid9, pilotRenderValue5, false, false, true, false);
         if (pilotRenderLimit >= 0.997F) {
            amountHandle2.handle(handleValue, strokeWidth);
         } else {
            amountHandle2.handleAmountForFloat(handleValue, strokeWidth, -90.0F + 180.0F * pilotRenderLimit, 360.0F * pilotRenderLimit);
         }

         amountHandle2.updateAmountPanel();
      }
   }

   private void renderOffscreenArrow(DrawContext context, VertexTextureFont vertexTextureFont, float pilotRenderValue, float scaledHeight, long nanoTime) {
      float angle = Float.isNaN(this.markerAngleRadians) ? (float) (Math.PI / 2) : this.markerAngleRadians;
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float fontScaled = pilotRenderValue * 0.5F;
      float nowScaled = scaledHeight * 0.5F;
      float pilotRenderValue4 = 11.0F;
      float pilotRenderBound = cos > 1.0E-6F ? (pilotRenderValue - 14.0F - pilotRenderValue4 - fontScaled) / cos : (cos < -1.0E-6F ? (14.0F + pilotRenderValue4 - fontScaled) / cos : Float.MAX_VALUE);
      float pilotRenderBound2 = sin > 1.0E-6F ? (scaledHeight - 52.0F - pilotRenderValue4 - nowScaled) / sin : (sin < -1.0E-6F ? (14.0F + pilotRenderValue4 - nowScaled) / sin : Float.MAX_VALUE);
      float max5 = Math.max(0.0F, Math.min(pilotRenderBound, pilotRenderBound2));
      float pilotRenderValue6 = 1.5F * (float)Math.sin(nanoTime / 1.0E9 * Math.PI * 2.0 / 1.2);
      float pilotRenderValue7 = fontScaled + cos * (max5 + pilotRenderValue6);
      float pilotRenderValue8 = nowScaled + sin * (max5 + pilotRenderValue6);
      int matrixStackValue = (CardMatrixUtils.isModernFlag() ? 102 : 184) << 24 | 1053465;
      float snapToPixelGrid10 = snapToPixelGrid(pilotRenderValue7 - 11.0F);
      float snapToPixelGrid11 = snapToPixelGrid(pilotRenderValue8 - 11.0F);
      MatrixPeekUtilsRender.render(context, snapToPixelGrid10, snapToPixelGrid11, 22.0F, 22.0F, 11.0F, matrixStackValue, 788529151);
      AmountHandle amountHandle = new AmountHandle(snapToPixelGrid10 + 11.0F, snapToPixelGrid11 + 11.0F, this.accentColor, false, true, false, false);
      amountHandle.handleAmount((float)Math.toDegrees(angle) + 180.0F, 0.0F, 5.0F);
      amountHandle.updateAmountPanel();
      if (!this.distanceText.isEmpty()) {
         float pilotRenderFactor = 0.74F;
         float materialFactor = (float)Math.ceil(this.distanceTextWidth * pilotRenderFactor + 10.0F);
         float pilotRenderFactor3 = 12.0F;
         float pilotRenderValue12 = Math.abs(cos) * materialFactor * 0.5F + Math.abs(sin) * pilotRenderFactor3 * 0.5F;
         float pilotRenderValue13 = pilotRenderValue7 - cos * (15.0F + pilotRenderValue12);
         float pilotRenderValue14 = pilotRenderValue8 - sin * (15.0F + pilotRenderValue12);
         float matrixPeekValue = snapToPixelGrid(clampFloat(pilotRenderValue13 - materialFactor / 2.0F, 2.0F, Math.max(2.0F, pilotRenderValue - materialFactor - 2.0F)));
         float configsLikeValue = snapToPixelGrid(clampFloat(pilotRenderValue14 - pilotRenderFactor3 / 2.0F, 2.0F, Math.max(2.0F, scaledHeight - pilotRenderFactor3 - 2.0F)));
         MatrixPeekUtilsRender.render(context, matrixPeekValue, configsLikeValue, materialFactor, pilotRenderFactor3, pilotRenderFactor3 / 2.0F, matrixStackValue, 788529151);
         this.drawDigitAlignedText(vertexTextureFont, this.distanceText, matrixPeekValue + 5.0F, this.getTextBaselineY(configsLikeValue, pilotRenderFactor3, pilotRenderFactor), pilotRenderFactor, OFF_WHITE);
      }
   }

   private void storeClampedPoint(double[] pilotRenderValues, int pilotRenderValue, double destinationX, double pilotRenderTotal, double destinationZ) {
      double pilotRenderFactor = destinationX - this.cameraX;
      double pilotRenderFactor2 = pilotRenderTotal - this.cameraY;
      double pilotRenderFactor3 = destinationZ - this.cameraZ;
      double distance = Math.sqrt(pilotRenderFactor * pilotRenderFactor + pilotRenderFactor2 * pilotRenderFactor2 + pilotRenderFactor3 * pilotRenderFactor3);
      if (distance > this.maxRenderDistance) {
         double pilotRenderFactor4 = this.maxRenderDistance / distance;
         destinationX = this.cameraX + pilotRenderFactor * pilotRenderFactor4;
         pilotRenderTotal = this.cameraY + pilotRenderFactor2 * pilotRenderFactor4;
         destinationZ = this.cameraZ + pilotRenderFactor3 * pilotRenderFactor4;
      }

      pilotRenderValues[pilotRenderValue * 3] = destinationX;
      pilotRenderValues[pilotRenderValue * 3 + 1] = pilotRenderTotal;
      pilotRenderValues[pilotRenderValue * 3 + 2] = destinationZ;
   }

   private static long quantizeDistance(double pilotRenderLimit) {
      if (!(pilotRenderLimit >= 0.0)) {
         return -1L;
      } else if (pilotRenderLimit < 1000.0) {
         return Math.round(pilotRenderLimit);
      } else {
         return pilotRenderLimit < 100000.0 ? 1000000L + Math.round(pilotRenderLimit / 100.0) : 2000000000L + Math.round(pilotRenderLimit / 1000.0);
      }
   }

   private static double square(double pilotRenderFactor) {
      return pilotRenderFactor * pilotRenderFactor;
   }

   private void renderDestinationBeam(PilotRender.Scene scene, float cameraFactor, double cameraValue) {
      double destinationX = scene.destinationX;
      double destinationZ = scene.destinationZ;
      double destinationAmounts = getDestinationY(scene);
      double sqrt = Math.sqrt(square(destinationX - this.cameraX) + square(destinationZ - this.cameraZ));
      float cameraValue3 = cameraFactor * smoothstepForDouble((sqrt - 2.0) / 10.0) * (float)(0.85 + 0.15 * cameraValue);
      if (!(cameraValue3 < 0.003F)) {
         double cameraValue4 = destinationAmounts + 0.05;
         double max5 = Math.max(destinationAmounts + 220.0, 384.0);

         for (int i = 0; i < 7; i++) {
            double cameraFactor2 = BEAM_HEIGHT_FRACTIONS[i];
            this.storeClampedPoint(this.beamPoints, i, destinationX, cameraValue4 + (max5 - cameraValue4) * cameraFactor2, destinationZ);
            float cameraFactor3 = GLOW_FADE_STEPS[i];
            this.beamColors[i] = MatrixAmountUtils.getValueInt(
               MatrixAmountUtils.getValue(this.accentTintColor, this.accentColor, (float)Math.min(1.0, cameraFactor2 * 2.2)), 0.95F * cameraValue3 * cameraFactor3
            );
            this.beamGlowColors[i] = MatrixAmountUtils.getValueInt(this.accentColor, 0.18F * cameraValue3 * cameraFactor3);
         }

         VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();
         float cameraValue6 = 14.0F * this.lineWidthScale;
         float planeValue = 3.0F * this.lineWidthScale;
         VectorPlaneUtils.handleAmountsPlane(buffer, this.worldMatrix, this.camera, this.beamPoints, this.beamGlowColors, 7, cameraValue6, cameraValue6, false, false);
         VectorPlaneUtils.handleAmountsPlane(buffer, this.worldMatrix, this.camera, this.beamPoints, this.beamColors, 7, planeValue, 1.25F, false, false);

         for (int j = 0; j < 3; j++) {
            double cameraValue8 = j / 2.0;
            this.storeClampedPoint(this.beamTipPoints, j, destinationX, cameraValue4 + 3.0 * cameraValue8, destinationZ);
            this.beamTipColors[j] = MatrixAmountUtils.getValueInt(-1, 0.9F * cameraValue3 * (1.0F - (float)cameraValue8) * (1.0F - 0.35F * (float)cameraValue8));
         }

         VectorPlaneUtils.handleAmountsPlane(buffer, this.worldMatrix, this.camera, this.beamTipPoints, this.beamTipColors, 3, 4.0F * this.lineWidthScale, 1.25F, true, false);
      }
   }

   private static void drawScaledText(VertexTextureFont vertexTextureFont, String pilotRenderText, float pilotRenderValue, float textBaselineY, float x2, Color color) {
      if (pilotRenderText != null && !pilotRenderText.isEmpty()) {
         if (Math.abs(x2 - 1.0F) < 5.0E-4F) {
            vertexTextureFont.handleForStringFloat(pilotRenderText, pilotRenderValue, textBaselineY, color);
         } else {
            MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
            matrixStack.push();

            try {
               matrixStack.scale(x2, x2, 1.0F);
               vertexTextureFont.handleForStringFloat(pilotRenderText, pilotRenderValue / x2, textBaselineY / x2, color);
            } finally {
               matrixStack.pop();
            }
         }
      }
   }

   private static float wrapAngleRadians(float pilotRenderValue) {
      double pilotRenderValue2 = Math.PI * 2;
      double pilotRenderTotal = (pilotRenderValue + Math.PI) % pilotRenderValue2;
      if (pilotRenderTotal < 0.0) {
         pilotRenderTotal += pilotRenderValue2;
      }

      return (float)(pilotRenderTotal - Math.PI);
   }

   private float getCharWidth(VertexTextureFont vertexTextureFont, char index) {
      if (index < CHAR_STRINGS.length && CHAR_STRINGS[index] != null) {
         float textsAmounts = this.charWidthCache[index];
         if (Float.isNaN(textsAmounts)) {
            textsAmounts = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(CHAR_STRINGS[index]);
            this.charWidthCache[index] = textsAmounts;
         }

         return textsAmounts;
      } else {
         return 0.0F;
      }
   }

   private int buildSmoothedRoute(int pilotRenderLimit) {
      int pilotRenderValue = 0;

      for (int i = 0; i < pilotRenderLimit; i++) {
         int index = i * 3;
         if (i != 0 && i != pilotRenderLimit - 1) {
            int index2 = index - 3;
            int index3 = index + 3;
            double pilotRenderFactor = this.destinationCurvePoints[index] - this.destinationCurvePoints[index2];
            double pilotRenderFactor2 = this.destinationCurvePoints[index + 1] - this.destinationCurvePoints[index2 + 1];
            double pilotRenderFactor3 = this.destinationCurvePoints[index + 2] - this.destinationCurvePoints[index2 + 2];
            double pilotRenderFactor4 = this.destinationCurvePoints[index3] - this.destinationCurvePoints[index];
            double pilotRenderFactor5 = this.destinationCurvePoints[index3 + 1] - this.destinationCurvePoints[index + 1];
            double pilotRenderFactor6 = this.destinationCurvePoints[index3 + 2] - this.destinationCurvePoints[index + 2];
            double distance = Math.sqrt(pilotRenderFactor * pilotRenderFactor + pilotRenderFactor2 * pilotRenderFactor2 + pilotRenderFactor3 * pilotRenderFactor3);
            double goalBound = Math.sqrt(pilotRenderFactor4 * pilotRenderFactor4 + pilotRenderFactor5 * pilotRenderFactor5 + pilotRenderFactor6 * pilotRenderFactor6);
            if (!(distance < 1.0E-6) && !(goalBound < 1.0E-6)) {
               double pilotRenderFactor7 = pilotRenderFactor / distance;
               double pilotRenderFactor8 = pilotRenderFactor2 / distance;
               double pilotRenderFactor9 = pilotRenderFactor3 / distance;
               double pilotRenderFactor10 = pilotRenderFactor4 / goalBound;
               double pilotRenderFactor11 = pilotRenderFactor5 / goalBound;
               double pilotRenderFactor12 = pilotRenderFactor6 / goalBound;
               double min13 = Math.min(24.0, 0.4 * Math.min(distance, goalBound));
               if (!(pilotRenderFactor7 * pilotRenderFactor10 + pilotRenderFactor8 * pilotRenderFactor11 + pilotRenderFactor9 * pilotRenderFactor12 > 0.9995) && !(min13 < 0.05)) {
                  double pilotRenderFactor14 = this.destinationCurvePoints[index] - pilotRenderFactor7 * min13;
                  double pilotRenderFactor15 = this.destinationCurvePoints[index + 1] - pilotRenderFactor8 * min13;
                  double pilotRenderFactor16 = this.destinationCurvePoints[index + 2] - pilotRenderFactor9 * min13;
                  double pilotRenderFactor17 = this.destinationCurvePoints[index] + pilotRenderFactor10 * min13;
                  double pilotRenderFactor18 = this.destinationCurvePoints[index + 1] + pilotRenderFactor11 * min13;
                  double pilotRenderFactor19 = this.destinationCurvePoints[index + 2] + pilotRenderFactor12 * min13;

                  for (int j = 0; j <= 8; j++) {
                     double pilotRenderFactor20 = j / 8.0;
                     double pilotRenderFactor21 = 1.0 - pilotRenderFactor20;
                     pilotRenderValue = this.appendRoutePoint(
                        pilotRenderValue,
                        pilotRenderFactor21 * pilotRenderFactor21 * pilotRenderFactor14 + 2.0 * pilotRenderFactor21 * pilotRenderFactor20 * this.destinationCurvePoints[index] + pilotRenderFactor20 * pilotRenderFactor20 * pilotRenderFactor17,
                        pilotRenderFactor21 * pilotRenderFactor21 * pilotRenderFactor15 + 2.0 * pilotRenderFactor21 * pilotRenderFactor20 * this.destinationCurvePoints[index + 1] + pilotRenderFactor20 * pilotRenderFactor20 * pilotRenderFactor18,
                        pilotRenderFactor21 * pilotRenderFactor21 * pilotRenderFactor16 + 2.0 * pilotRenderFactor21 * pilotRenderFactor20 * this.destinationCurvePoints[index + 2] + pilotRenderFactor20 * pilotRenderFactor20 * pilotRenderFactor19
                     );
                  }
               } else {
                  pilotRenderValue = this.appendRoutePoint(pilotRenderValue, this.destinationCurvePoints[index], this.destinationCurvePoints[index + 1], this.destinationCurvePoints[index + 2]);
               }
            } else {
               pilotRenderValue = this.appendRoutePoint(pilotRenderValue, this.destinationCurvePoints[index], this.destinationCurvePoints[index + 1], this.destinationCurvePoints[index + 2]);
            }
         } else {
            pilotRenderValue = this.appendRoutePoint(pilotRenderValue, this.destinationCurvePoints[index], this.destinationCurvePoints[index + 1], this.destinationCurvePoints[index + 2]);
         }
      }

      return pilotRenderValue;
   }

   private int appendPathSample(int index, double pilotRenderValue, double pilotRenderTotal, double destinationZ, double pilotRenderValue3, double pilotRenderValue4, float pilotRenderFactor) {
      double pilotRenderFactor2 = pilotRenderValue - this.cameraX;
      double deltaY = pilotRenderTotal - this.cameraY;
      double deltaZ = destinationZ - this.cameraZ;
      double distance = Math.sqrt(pilotRenderFactor2 * pilotRenderFactor2 + deltaY * deltaY + deltaZ * deltaZ);
      pilotRenderTotal += 0.045 + pilotRenderFactor * Math.min(distance, this.maxRenderDistance) / this.framebufferHeight;
      this.storeClampedPoint(this.pathPoints, index, pilotRenderValue, pilotRenderTotal, destinationZ);
      this.pathDistanceFromStart[index] = pilotRenderValue3;
      this.pathDistanceToEnd[index] = pilotRenderValue4;
      return index + 1;
   }

   static {
      for (int i = 32; i < CHAR_STRINGS.length; i++) {
         CHAR_STRINGS[i] = String.valueOf((char)i);
      }
   }

   public void renderHud(DrawContext context, float scaledWidthValue, PilotRender.Scene scene) {
      if (!this.hudRenderFailed && context != null && scene != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance.player != null && instance.world != null && instance.gameRenderer != null) {
            if (!instance.options.hudHidden && instance.currentScreen == null && CorzClient.fontManager != null) {
               boolean scaledWidthFlag = false;

               try {
                  CorzClient.fontManager.setMatrixStack(this.matrixStack);
                  MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
                  double scaleFactor = instance.getWindow().getScaleFactor();
                  int calculateScaleFactor = instance.getWindow().calculateScaleFactor(ShowHUD.getMax(instance), instance.forcesUnicodeFont());
                  float scaledWidthDivisor = (float)(calculateScaleFactor / scaleFactor);
                  if (!(scaledWidthDivisor > 0.05F) || !Float.isFinite(scaledWidthDivisor)) {
                     scaledWidthDivisor = 1.0F;
                  }

                  float scaledWidth = instance.getWindow().getScaledWidth();
                  float scaledHeight = instance.getWindow().getScaledHeight();
                  if (Math.abs(scaledWidthDivisor - 1.0F) > 1.0E-4F) {
                     scaledWidth = Math.round(scaledWidth / scaledWidthDivisor);
                     scaledHeight = Math.round(scaledHeight / scaledWidthDivisor);
                     CorzRoundedUtils.scaledWidth = scaledWidth;
                     CorzRoundedUtils.scaledHeight = scaledHeight;
                     scaledWidthFlag = true;
                  }

                  this.renderDestinationHud(context, instance, scene, scaledWidthDivisor, scaledWidth, scaledHeight);
               } catch (Throwable e) {
                  this.hudRenderFailed = true;
                  this.matrixStack = new MatrixStack();
               } finally {
                  if (scaledWidthFlag) {
                     CorzRoundedUtils.scaledWidth = 0.0F;
                     CorzRoundedUtils.scaledHeight = 0.0F;
                  }
               }
            }
         }
      }
   }

   private static float snapToPixelGrid(float pilotRenderValue) {
      float pilotRenderLimit = MatrixPeekUtilsRender.getAmount();
      return pilotRenderLimit > 0.0F ? Math.round(pilotRenderValue / pilotRenderLimit) * pilotRenderLimit : pilotRenderValue;
   }

   public PilotRender() {
      this.pathCumulativeLengths = new double[580];
      this.pathPoints = new double[3072];
      this.pathDistanceFromStart = new double[1024];
      this.pathDistanceToEnd = new double[1024];
      this.pathCoreColors = new int[1024];
      this.pathFillColors = new int[1024];
      this.pathOutlineColors = new int[1024];
      this.beamPoints = new double[21];
      this.beamTipPoints = new double[9];
      this.beamColors = new int[7];
      this.beamGlowColors = new int[7];
      this.beamTipColors = new int[3];
      this.ringVertices = new double[147];
      this.pulseRingVertices = new double[147];
      this.ringColors = new int[49];
      this.pathSampleBuffer = new double[3];
      this.viewportRect = new int[4];
      this.viewSpacePoint = new Vector4f();
      this.projectedPoint = new Vector3f();
      this.cameraRelativeVec = new Vector3f();
      this.projectionMatrix = new Matrix4f();
      this.charWidthCache = new float[256];
      this.matrixStack = new MatrixStack();
      this.markerAngleRadians = Float.NaN;
      this.destinationText = "";
      this.distanceText = "";
      this.etaText = "";
      this.lastDistanceKey = Long.MIN_VALUE;
      this.lastEtaSeconds = Long.MIN_VALUE;
      this.distanceTextColor = OFF_WHITE;
   }

   public void resetState() {
      this.lastNanoTime = 0L;
      this.lastHudNanoTime = 0L;
      this.worldFadeProgress = 0.0F;
      this.panelFadeProgress = 0.0F;
      this.markerFadeProgress = 0.0F;
      this.markerAngleRadians = Float.NaN;
      this.lastDistanceKey = Long.MIN_VALUE;
      this.lastEtaSeconds = Long.MIN_VALUE;
      this.lastSceneLabel = null;
      this.fontRenderer = null;
   }

   private static void fillCircleVertices(double[] pilotRenderValues, double pilotRenderValue, double pilotRenderValue2, double destinationZ, double pilotRenderFactor) {
      for (int i = 0; i <= 48; i++) {
         double angle = (Math.PI * 2) * i / 48.0;
         pilotRenderValues[i * 3] = pilotRenderValue + Math.cos(angle) * pilotRenderFactor;
         pilotRenderValues[i * 3 + 1] = pilotRenderValue2;
         pilotRenderValues[i * 3 + 2] = destinationZ + Math.sin(angle) * pilotRenderFactor;
      }
   }

   private void renderOnscreenMarker(
      DrawContext context, VertexTextureFont vertexTextureFont, float colorValue, float colorValue2, float clamp, float pilotRenderLimit, float pilotRenderValue, float scaledHeight, boolean colorFlag, long nanoTime
   ) {
      float colorFactor = 0.9F * clamp;
      float pilotRenderFactor = 0.8F * clamp;
      float snapToPixelGrid8 = snapToPixelGrid(16.0F * clamp);
      float colorValue9 = 6.0F * clamp;
      float colorValue10 = 8.0F * clamp;
      boolean empty2 = !this.etaText.isEmpty();
      float materialFactor = (float)Math.ceil(
         colorValue9
            + colorValue10
            + 5.0F * clamp
            + this.destinationTextWidth * colorFactor
            + 6.0F * clamp
            + this.distanceTextWidth * colorFactor
            + (empty2 ? 4.0F * clamp * 2.0F + this.dotSeparatorWidth * pilotRenderFactor + this.etaTextWidth * pilotRenderFactor : 0.0F)
            + colorValue9
      );
      float matrixPeekValue = snapToPixelGrid(clampFloat(colorValue - materialFactor / 2.0F, 4.0F, Math.max(4.0F, pilotRenderValue - materialFactor - 4.0F)));
      float configsLikeValue = snapToPixelGrid(clampFloat(colorValue2 - snapToPixelGrid8 / 2.0F, 4.0F, Math.max(4.0F, scaledHeight - snapToPixelGrid8 - 4.0F)));
      int matrixStackValue = (CardMatrixUtils.isModernFlag() ? 102 : 184) << 24 | 1053465;
      MatrixPeekUtilsRender.render(context, matrixPeekValue, configsLikeValue, materialFactor, snapToPixelGrid8, Math.min(6.0F * clamp, snapToPixelGrid8 / 2.0F), matrixStackValue, 788529151);
      float colorValue15 = matrixPeekValue + colorValue9;
      float matrixLimit = colorFlag ? 0.75F + 0.25F * (float)(0.5 + 0.5 * Math.sin(nanoTime / 1.0E9 * Math.PI * 2.0 / 0.9)) : 1.0F;
      drawProgressRing(colorValue15 + colorValue10 / 2.0F, configsLikeValue + snapToPixelGrid8 / 2.0F, colorValue10, pilotRenderLimit, MatrixAmountUtils.getValue(this.accentColor, matrixLimit));
      float colorValue17 = colorValue15 + (colorValue10 + 5.0F * clamp);
      float textBaselineY = this.getTextBaselineY(configsLikeValue, snapToPixelGrid8, colorFactor);
      drawScaledText(vertexTextureFont, this.destinationText, colorValue17, textBaselineY, colorFactor, OFF_WHITE);
      float colorValue19 = colorValue17 + (this.destinationTextWidth * colorFactor + 6.0F * clamp);
      this.drawDigitAlignedText(vertexTextureFont, this.distanceText, colorValue19, textBaselineY, colorFactor, this.distanceTextColor);
      float colorValue20 = colorValue19 + this.distanceTextWidth * colorFactor;
      if (empty2) {
         float colorValue21 = colorValue20 + 4.0F * clamp;
         float textBaselineY22 = this.getTextBaselineY(configsLikeValue, snapToPixelGrid8, pilotRenderFactor);
         drawScaledText(vertexTextureFont, "\u00b7", colorValue21, textBaselineY22, pilotRenderFactor, GRAY);
         float pilotRenderTotal = colorValue21 + (this.dotSeparatorWidth * pilotRenderFactor + 4.0F * clamp);
         this.drawDigitAlignedText(vertexTextureFont, this.etaText, pilotRenderTotal, textBaselineY22, pilotRenderFactor, GRAY);
      }
   }

   private float getScreenDirectionAngle(double vectorMatrixValue, double vectorMatrixValue2, double vectorMatrixValue3, float vectorMatrixFactor, float scaledHeight) {
      this.cameraRelativeVec.set((float)(vectorMatrixValue - this.cameraX), (float)(vectorMatrixValue2 - this.cameraY), (float)(vectorMatrixValue3 - this.cameraZ));
      NameTags.MATRIX4F3.transformDirection(this.cameraRelativeVec);
      float x = this.cameraRelativeVec.x * NameTags.MATRIX4F.m00() * vectorMatrixFactor;
      float y = -this.cameraRelativeVec.y * NameTags.MATRIX4F.m11() * scaledHeight;
      return Float.isFinite(x) && Float.isFinite(y) && !(Math.abs(x) + Math.abs(y) < 1.0E-5F) ? (float)Math.atan2(y, x) : Float.NaN;
   }

   private float getTextWidth(VertexTextureFont vertexTextureFont, String pilotRenderText) {
      float textsAmount = 0.0F;
      int index = 0;

      for (int i = pilotRenderText.length(); index < i; index++) {
         char charAt = pilotRenderText.charAt(index);
         if (charAt < CHAR_STRINGS.length && CHAR_STRINGS[charAt] != null) {
            textsAmount += charAt >= '0' && charAt <= '9' ? this.digitWidth : this.getCharWidth(vertexTextureFont, charAt);
         }
      }

      return textsAmount;
   }

   public void renderWorld(QueueCommand queueCommand, PilotRender.Scene scene) {
      if (!this.worldRenderFailed && scene != null) {
         try {
            this.renderWorldOverlay(queueCommand, scene);
         } catch (Throwable e2) {
            this.worldRenderFailed = true;
         } finally {
            this.camera = null;
            this.worldMatrix = null;
         }
      }
   }

   private int appendRoutePoint(int index, double pilotRenderValue, double pilotRenderValue2, double pilotRenderValue3) {
      if (index >= 580) {
         return index;
      } else {
         if (index > 0) {
            double sqrt = Math.sqrt(
               square(pilotRenderValue - this.pathRawPoints[index * 3 - 3]) + square(pilotRenderValue2 - this.pathRawPoints[index * 3 - 2]) + square(pilotRenderValue3 - this.pathRawPoints[index * 3 - 1])
            );
            if (sqrt < 1.0E-4) {
               return index;
            }

            this.pathCumulativeLengths[index] = this.pathCumulativeLengths[index - 1] + sqrt;
         } else {
            this.pathCumulativeLengths[0] = 0.0;
         }

         this.pathRawPoints[index * 3] = pilotRenderValue;
         this.pathRawPoints[index * 3 + 1] = pilotRenderValue2;
         this.pathRawPoints[index * 3 + 2] = pilotRenderValue3;
         return index + 1;
      }
   }

   private double getDistanceLift(double pilotRenderValue, double destinationAmount, double destinationZ, float pilotRenderFactor) {
      double sqrt = Math.sqrt(square(pilotRenderValue - this.cameraX) + square(destinationAmount - this.cameraY) + square(destinationZ - this.cameraZ));
      return 0.045 + pilotRenderFactor * Math.min(sqrt, this.maxRenderDistance) / this.framebufferHeight;
   }

   private void renderDestinationRing(PilotRender.Scene scene, float destinationFactor, double destinationValue, double seconds) {
      if (isFiniteDouble(scene.destinationY)) {
         double destinationX = scene.destinationX;
         double destinationAmount = scene.destinationY;
         double destinationZ = scene.destinationZ;
         double sqrt = Math.sqrt(square(destinationX - this.cameraX) + square(destinationAmount - this.cameraY) + square(destinationZ - this.cameraZ));
         if (!(sqrt > this.maxRenderDistance)) {
            double pilotRenderFactor = 1.2 + 0.06 * destinationValue;
            if (!VertexBuilderUtils.canVector(destinationX, destinationAmount, destinationZ, (float)(pilotRenderFactor + 2.6 + 1.0))) {
               float destinationFactor2 = 1.0F - smoothstepForDouble((sqrt - this.maxRenderDistance * 0.8) / (this.maxRenderDistance * 0.2));
               float destinationFactor3 = scene.highlightDestination ? 1.3F : 1.0F;
               float destinationFactor4 = (0.72F + 0.28F * (float)(0.5 + 0.5 * destinationValue)) * destinationFactor3;
               float destinationFactor5 = destinationFactor * destinationFactor2;
               if (!(destinationFactor5 < 0.003F)) {
                  float planeValue = (scene.highlightDestination ? 2.2F : 1.8F) * this.lineWidthScale;
                  double destinationValue6 = destinationAmount + this.getDistanceLift(destinationX, destinationAmount, destinationZ, 0.5F * (planeValue + 1.25F));
                  fillCircleVertices(this.ringVertices, destinationX, destinationValue6, destinationZ, pilotRenderFactor);
                  VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();
                  VertexConsumer buffer2 = VertexBuilderUtils.getBuffer();
                  Arrays.fill(this.ringColors, MatrixAmountUtils.getValueInt(this.accentColor, 0.3F * destinationFactor5 * destinationFactor4));
                  VectorPlaneUtils.handleAmountsPlane(buffer, this.worldMatrix, this.camera, this.ringVertices, this.ringColors, 49, planeValue, 1.25F, false, false);
                  Arrays.fill(this.ringColors, MatrixAmountUtils.getValueInt(this.accentTintColor, Math.min(1.0F, 0.95F * destinationFactor5 * destinationFactor4)));
                  VectorPlaneUtils.handleAmountsPlane(buffer2, this.worldMatrix, this.camera, this.ringVertices, this.ringColors, 49, planeValue, 1.25F, false, false);
                  int valueInt = MatrixAmountUtils.getValueInt(this.accentColor, (scene.highlightDestination ? 0.2F : 0.14F) * destinationFactor5 * destinationFactor4);
                  int valueInt8 = MatrixAmountUtils.getValueInt(this.accentColor, (scene.highlightDestination ? 0.06F : 0.03F) * destinationFactor5);
                  float amountX = VertexBuilderUtils.getAmountX(destinationX);
                  float deltaY = VertexBuilderUtils.getAmount(destinationValue6 - 0.004);
                  float amountForDouble = VertexBuilderUtils.getAmountForDouble(destinationZ);

                  for (int i = 0; i < 48; i++) {
                     int index = i * 3;
                     int index2 = (i + 1) * 3;
                     VertexBuilderUtils.handleVertexColor(
                        buffer2,
                        this.worldMatrix,
                        amountX,
                        deltaY,
                        amountForDouble,
                        valueInt8,
                        VertexBuilderUtils.getAmountX(this.ringVertices[index]),
                        deltaY,
                        VertexBuilderUtils.getAmountForDouble(this.ringVertices[index + 2]),
                        valueInt,
                        VertexBuilderUtils.getAmountX(this.ringVertices[index2]),
                        deltaY,
                        VertexBuilderUtils.getAmountForDouble(this.ringVertices[index2 + 2]),
                        valueInt,
                        amountX,
                        deltaY,
                        amountForDouble,
                        valueInt8
                     );
                  }

                  if (scene.highlightDestination) {
                     double destinationValue12 = seconds % 1.6 / 1.6;
                     float destinationFactor6 = (float)((1.0 - destinationValue12) * (1.0 - destinationValue12)) * smoothstepForDouble(destinationValue12 / 0.08);
                     if (!(destinationFactor6 * destinationFactor5 < 0.004F)) {
                        fillCircleVertices(this.pulseRingVertices, destinationX, destinationValue6, destinationZ, pilotRenderFactor + 2.6 * destinationValue12);
                        Arrays.fill(this.ringColors, MatrixAmountUtils.getValueInt(this.accentColor, 0.25F * destinationFactor5 * destinationFactor6));
                        VectorPlaneUtils.handleAmountsPlane(buffer, this.worldMatrix, this.camera, this.pulseRingVertices, this.ringColors, 49, planeValue, 1.25F, false, false);
                        Arrays.fill(this.ringColors, MatrixAmountUtils.getValueInt(this.accentTintColor, 0.8F * destinationFactor5 * destinationFactor6));
                        VectorPlaneUtils.handleAmountsPlane(buffer2, this.worldMatrix, this.camera, this.pulseRingVertices, this.ringColors, 49, planeValue, 1.25F, false, false);
                     }
                  }
               }
            }
         }
      }
   }

   private void renderDestinationHud(DrawContext context, MinecraftClient client, PilotRender.Scene scene, float pilotRenderDivisor, float pilotRenderValue, float scaledHeight) {
      long nanoTime = System.nanoTime();
      float pilotRenderValue3 = this.lastHudNanoTime == 0L ? 0.016F : Math.min(0.1F, (float)(nanoTime - this.lastHudNanoTime) / 1.0E9F);
      this.lastHudNanoTime = nanoTime;
      boolean pilotRenderFlag = scene.destinationActive && isFiniteDouble(scene.destinationX) && isFiniteDouble(scene.destinationZ);
      this.panelFadeProgress = pilotRenderFlag ? Math.min(1.0F, this.panelFadeProgress + pilotRenderValue3 / 0.26F) : Math.max(0.0F, this.panelFadeProgress - pilotRenderValue3 / 0.32F);
      if (this.panelFadeProgress <= 0.0F) {
         this.markerAngleRadians = Float.NaN;
         this.markerFadeProgress = 0.0F;
      } else if (isFiniteDouble(scene.destinationX) && isFiniteDouble(scene.destinationZ)) {
         Camera camera = client.gameRenderer.getCamera();
         if (camera != null) {
            Vec3d cameraPos = camera.getCameraPos();
            this.cameraX = cameraPos.x;
            this.cameraY = cameraPos.y;
            this.cameraZ = cameraPos.z;
            this.maxRenderDistance = getMaxRenderDistance(client);
            float pilotRenderFactor = MatrixAmountUtils.getAmountForFloat(this.panelFadeProgress);
            this.accentColor = 0xFF000000 | MatrixEspUtils.releaseRightValue;
            double destinationX = scene.destinationX;
            double destinationAmountsOffset = getDestinationY(scene) + 2.5;
            double destinationZ = scene.destinationZ;
            double pilotRenderFactor2 = destinationX - this.cameraX;
            double pilotRenderFactor3 = destinationAmountsOffset - this.cameraY;
            double pilotRenderFactor4 = destinationZ - this.cameraZ;
            double distance = Math.sqrt(pilotRenderFactor2 * pilotRenderFactor2 + pilotRenderFactor3 * pilotRenderFactor3 + pilotRenderFactor4 * pilotRenderFactor4);
            double pilotRenderFactor5 = distance > this.maxRenderDistance ? this.maxRenderDistance / distance : 1.0;
            double vectorWindowValue = this.cameraX + pilotRenderFactor2 * pilotRenderFactor5;
            double pilotRenderValue6 = this.cameraY + pilotRenderFactor3 * pilotRenderFactor5;
            double pilotRenderValue7 = this.cameraZ + pilotRenderFactor4 * pilotRenderFactor5;
            boolean pilotRenderFlag2 = false;
            if (this.projectWorldPoint(client, vectorWindowValue, pilotRenderValue6, pilotRenderValue7) && this.windowDepth > 0.0 && this.windowDepth < 1.0) {
               float amount = (float)(this.windowScreenX / pilotRenderDivisor);
               float amount3 = (float)(this.windowScreenY / pilotRenderDivisor);
               if (amount >= 18.0F && amount <= pilotRenderValue - 18.0F && amount3 >= 18.0F && amount3 <= scaledHeight - 18.0F) {
                  pilotRenderFlag2 = true;
                  this.markerScreenX = amount;
                  this.markerScreenY = amount3;
               }
            }

            if (pilotRenderFlag2 && this.markerFadeProgress <= 0.0F && Float.isNaN(this.markerAngleRadians)) {
               this.markerFadeProgress = 1.0F;
            }

            this.markerFadeProgress = pilotRenderFlag2 ? Math.min(1.0F, this.markerFadeProgress + pilotRenderValue3 / 0.15F) : Math.max(0.0F, this.markerFadeProgress - pilotRenderValue3 / 0.15F);
            float screenDirectionAngle = this.getScreenDirectionAngle(vectorWindowValue, pilotRenderValue6, pilotRenderValue7, pilotRenderValue, scaledHeight);
            if (Float.isNaN(screenDirectionAngle)) {
               screenDirectionAngle = Float.isNaN(this.markerAngleRadians) ? (float) (Math.PI / 2) : this.markerAngleRadians;
            }

            if (Float.isNaN(this.markerAngleRadians)) {
               this.markerAngleRadians = screenDirectionAngle;
            } else {
               this.markerAngleRadians = wrapAngleRadians(this.markerAngleRadians + wrapAngleRadians(screenDirectionAngle - this.markerAngleRadians) * (1.0F - (float)Math.exp(-pilotRenderValue3 / 0.07F)));
            }

            ClientPlayerEntity player = client.player;
            double destinationPitValue = isFiniteDouble(scene.distanceToDestination) && scene.distanceToDestination >= 0.0
               ? scene.distanceToDestination
               : Math.sqrt(square(scene.destinationX - player.getX()) + square(scene.destinationZ - player.getZ()));
            VertexTextureFont fontRenderer = CorzClient.fontRenderer();
            if (fontRenderer != null) {
               this.updateHudTexts(fontRenderer, scene, destinationPitValue);
               float pilotRenderLimit = scene.totalRouteDistance > 1.0E-6 && isFiniteDouble(scene.totalRouteDistance) ? clampForFloat((float)(1.0 - destinationPitValue / scene.totalRouteDistance)) : 0.0F;
               float amountForFloat = MatrixAmountUtils.getAmountForFloat(this.markerFadeProgress);
               float amountForFloat7 = MatrixAmountUtils.getAmountForFloat(1.0F - this.markerFadeProgress);
               if (amountForFloat * pilotRenderFactor > 0.01F) {
                  float clamp = MathHelper.clamp(1.12F - (float)distance / 56.0F, 0.8F, 1.0F);
                  float panelOverlay = CorzRoundedUtils.getPanelOverlay(amountForFloat * pilotRenderFactor);

                  try {
                     this.renderOnscreenMarker(context, fontRenderer, this.markerScreenX, this.markerScreenY, clamp, pilotRenderLimit, pilotRenderValue, scaledHeight, scene.highlightDestination, nanoTime);
                  } finally {
                     CorzRoundedUtils.handlePanelOverlay(panelOverlay);
                  }
               }

               if (amountForFloat7 * pilotRenderFactor > 0.01F) {
                  float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(amountForFloat7 * pilotRenderFactor);

                  try {
                     this.renderOffscreenArrow(context, fontRenderer, pilotRenderValue, scaledHeight, nanoTime);
                  } finally {
                     CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
                  }
               }
            }
         }
      }
   }

   private static float clampFloat(float pilotRenderLimit, float pilotRenderLimit2, float pilotRenderValue) {
      return pilotRenderLimit < pilotRenderLimit2 ? pilotRenderLimit2 : (pilotRenderLimit > pilotRenderValue ? pilotRenderValue : pilotRenderLimit);
   }

   private void updateHudTexts(VertexTextureFont fontRenderer, PilotRender.Scene scene, double destinationPitValue) {
      if (fontRenderer != this.fontRenderer) {
         this.fontRenderer = fontRenderer;
         Arrays.fill(this.charWidthCache, Float.NaN);
         this.digitWidth = this.getCharWidth(fontRenderer, '0');
         this.textVerticalOffset = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(0, 100) - 50.0F;
         this.lastSceneLabel = null;
         this.lastDistanceKey = Long.MIN_VALUE;
         this.lastEtaSeconds = Long.MIN_VALUE;
         this.dotSeparatorWidth = fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("\u00b7");
      }

      if (scene.destinationLabel != this.lastSceneLabel && (scene.destinationLabel == null || !scene.destinationLabel.equals(this.lastSceneLabel))) {
         this.lastSceneLabel = scene.destinationLabel;
         String destinationPitText = scene.destinationLabel != null && !scene.destinationLabel.isBlank() ? scene.destinationLabel : "Destination";
         String orDefault = CardStatusUtilsFlag.getOrDefault(destinationPitText);
         this.destinationText = fontRenderer.formatText(orDefault == null ? "" : orDefault, 140.0F);
         this.destinationTextWidth = fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.destinationText);
      }

      long time = quantizeDistance(destinationPitValue);
      if (time != this.lastDistanceKey) {
         this.lastDistanceKey = time;
         this.distanceText = Destination.getFormat(destinationPitValue);
         this.distanceTextWidth = this.getTextWidth(fontRenderer, this.distanceText);
      }

      long seconds = scene.etaSeconds < 0L ? -1L : scene.etaSeconds;
      if (seconds != this.lastEtaSeconds) {
         this.lastEtaSeconds = seconds;
         this.etaText = seconds < 0L ? "" : Destination.formatText(seconds);
         this.etaTextWidth = this.getTextWidth(fontRenderer, this.etaText);
      }

      int value = CardMatrixUtils.getValue(this.accentColor, -1, 0.35);
      if (value != this.lastTextColorArgb || this.distanceTextColor == OFF_WHITE) {
         this.lastTextColorArgb = value;
         this.distanceTextColor = new Color(value, true);
      }
   }

   private boolean projectWorldPoint(MinecraftClient client, double vectorWindowValue, double vectorWindowValue2, double vectorWindowValue3) {
      int framebufferWidth = client.getWindow().getFramebufferWidth();
      int framebufferHeight = client.getWindow().getFramebufferHeight();
      if (framebufferWidth > 0 && framebufferHeight > 0) {
         this.viewportRect[0] = 0;
         this.viewportRect[1] = 0;
         this.viewportRect[2] = framebufferWidth;
         this.viewportRect[3] = framebufferHeight;
         this.viewSpacePoint.set((float)(vectorWindowValue - this.cameraX), (float)(vectorWindowValue2 - this.cameraY), (float)(vectorWindowValue3 - this.cameraZ), 1.0F).mul(NameTags.MATRIX4F3);
         this.projectionMatrix.set(NameTags.MATRIX4F).mul(NameTags.MATRIX4_FMATRIX4F).project(this.viewSpacePoint.x, this.viewSpacePoint.y, this.viewSpacePoint.z, this.viewportRect, this.projectedPoint);
         double scaleFactor = client.getWindow().getScaleFactor();
         if (!(scaleFactor > 0.0)) {
            return false;
         } else {
            this.windowScreenX = this.projectedPoint.x / scaleFactor;
            this.windowScreenY = (framebufferHeight - this.projectedPoint.y) / scaleFactor;
            this.windowDepth = this.projectedPoint.z;
            return Double.isFinite(this.windowScreenX) && Double.isFinite(this.windowScreenY) && Double.isFinite(this.windowDepth);
         }
      } else {
         return false;
      }
   }

   private int collectRoutePoints(PilotRender.Scene scene, double destinationValue, double destinationValue2, double destinationValue3) {
      double[] destinationAmounts = scene.routePoints;
      int destinationLimit = Math.max(0, Math.min(scene.routePointCount, Math.min(64, destinationAmounts.length / 3)));
      int amountsDestination = 0;

      for (int i = 0; i < destinationLimit; i++) {
         double pilotRenderValue = destinationAmounts[i * 3];
         double destinationValue5 = destinationAmounts[i * 3 + 1];
         double destinationValue6 = destinationAmounts[i * 3 + 2];
         if (isFiniteDouble(pilotRenderValue)
            && isFiniteDouble(destinationValue5)
            && isFiniteDouble(destinationValue6)
            && (
               amountsDestination <= 0
                  || !(
                     square(pilotRenderValue - this.destinationCurvePoints[amountsDestination * 3 - 3])
                           + square(destinationValue5 - this.destinationCurvePoints[amountsDestination * 3 - 2])
                           + square(destinationValue6 - this.destinationCurvePoints[amountsDestination * 3 - 1])
                        < 1.0E-6
                  )
            )) {
            this.destinationCurvePoints[amountsDestination * 3] = pilotRenderValue;
            this.destinationCurvePoints[amountsDestination * 3 + 1] = destinationValue5;
            this.destinationCurvePoints[amountsDestination * 3 + 2] = destinationValue6;
            amountsDestination++;
         }
      }

      if (amountsDestination >= 2 && square(this.destinationCurvePoints[0] - destinationValue) + square(this.destinationCurvePoints[1] - destinationValue2) + square(this.destinationCurvePoints[2] - destinationValue3) < 576.0) {
         this.destinationCurvePoints[0] = destinationValue;
         this.destinationCurvePoints[1] = destinationValue2;
         this.destinationCurvePoints[2] = destinationValue3;
      }

      return amountsDestination;
   }

   private static boolean isFiniteDouble(double pilotRenderValue) {
      return !Double.isNaN(pilotRenderValue) && !Double.isInfinite(pilotRenderValue);
   }

   private void drawDigitAlignedText(VertexTextureFont vertexTextureFont, String pilotRenderText, float pilotRenderTotal, float pilotRenderValue, float pilotRenderFactor, Color color) {
      float pilotRenderValue2 = this.digitWidth * pilotRenderFactor;
      int index = 0;

      for (int i = pilotRenderText.length(); index < i; index++) {
         char charAt = pilotRenderText.charAt(index);
         String pilotRenderText2 = charAt < CHAR_STRINGS.length && CHAR_STRINGS[charAt] != null ? CHAR_STRINGS[charAt] : null;
         if (pilotRenderText2 != null) {
            if (charAt >= '0' && charAt <= '9') {
               drawScaledText(vertexTextureFont, pilotRenderText2, pilotRenderTotal + (pilotRenderValue2 - this.getCharWidth(vertexTextureFont, charAt) * pilotRenderFactor) / 2.0F, pilotRenderValue, pilotRenderFactor, color);
               pilotRenderTotal += pilotRenderValue2;
            } else {
               if (charAt != ' ') {
                  drawScaledText(vertexTextureFont, pilotRenderText2, pilotRenderTotal, pilotRenderValue, pilotRenderFactor, color);
               }

               pilotRenderTotal += this.getCharWidth(vertexTextureFont, charAt) * pilotRenderFactor;
            }
         }
      }
   }

   private static double getMaxRenderDistance(MinecraftClient client) {
      return Math.max(64.0, client.options.getClampedViewDistance() * 16.0 - 8.0);
   }

   private void renderRouteLine(int cameraValue, double pilotValue, double cameraValue3, double cameraValue4, float cameraFactor, double seconds) {
      int cameraLimit = this.buildSmoothedRoute(cameraValue);
      if (cameraLimit >= 2) {
         double cameraBound = this.pathCumulativeLengths[cameraLimit - 1];
         double min6 = Math.min(cameraBound, PolylinePathMath.getDistanceAlongPath(this.pathRawPoints, this.pathCumulativeLengths, cameraLimit, pilotValue, cameraValue3, cameraValue4) + 3.0);
         if (!(cameraBound - min6 < 0.75)) {
            float max7 = Math.max(1.5F, 2.6F * this.resolutionScale);
            float pilotRenderFactor = 0.5F * (max7 + 1.25F);
            int planeLimit = 0;
            double cameraLimit3 = min6;
            int index = PolylinePathMath.findSegmentIndex(this.pathCumulativeLengths, cameraLimit, min6) + 1;

            while (true) {
               PolylinePathMath.samplePositionAtDistance(this.pathRawPoints, this.pathCumulativeLengths, cameraLimit, cameraLimit3, this.pathSampleBuffer);
               double max9 = Math.max(0.0, cameraBound - cameraLimit3);
               double cameraValue10 = 1.5 * smoothstepForDouble(max9 / 12.0);
               planeLimit = this.appendPathSample(planeLimit, this.pathSampleBuffer[0], this.pathSampleBuffer[1] - cameraValue10, this.pathSampleBuffer[2], cameraLimit3 - min6, max9, pilotRenderFactor);
               if (cameraLimit3 >= cameraBound || planeLimit >= 1024) {
                  if (planeLimit < 2) {
                     return;
                  } else {
                     double cameraValue11 = seconds * 18.0;

                     for (int i = 0; i < planeLimit; i++) {
                        double cameraValue12 = this.pathDistanceFromStart[i];
                        double cameraValue13 = this.pathDistanceToEnd[i];
                        float cameraFactor2 = cameraFactor * smoothstepForDouble(cameraValue12 / 4.0);
                        double cameraFactor3 = 0.5 + 0.5 * Math.cos((cameraValue12 - cameraValue11) * Math.PI * 2.0 / 16.0);
                        double cameraFactor4 = cameraFactor3 * cameraFactor3;
                        float cameraValue14 = (float)(cameraFactor4 * cameraFactor4) * (1.0F - smoothstepForDouble((cameraValue12 - 24.0) / 40.0));
                        float smoothstepForDouble15 = smoothstepForDouble(1.0 - cameraValue13 / 96.0);
                        int matrixValue = MatrixAmountUtils.getValue(this.accentColor, -1, 0.28F * cameraValue14 + 0.5F * smoothstepForDouble15);
                        this.pathCoreColors[i] = MatrixAmountUtils.getValueInt(matrixValue, 0.92F * cameraFactor2);
                        this.pathFillColors[i] = MatrixAmountUtils.getValueInt(this.accentColor, (0.12F + 0.05F * cameraValue14) * cameraFactor2);
                        this.pathOutlineColors[i] = MatrixAmountUtils.getValueInt(this.accentColor, 0.3F * cameraFactor2);
                     }

                     VectorPlaneUtils.handleAmountsPlane(
                        VertexBuilderUtils.getBufferIMMEDIATE(), this.worldMatrix, this.camera, this.pathPoints, this.pathOutlineColors, planeLimit, max7, 1.25F, false, true
                     );
                     VertexConsumer buffer = VertexBuilderUtils.getBuffer();
                     float planeValue = max7 * 2.0F + 2.0F;
                     VectorPlaneUtils.handleAmountsPlane(buffer, this.worldMatrix, this.camera, this.pathPoints, this.pathFillColors, planeLimit, planeValue, planeValue, false, false);
                     VectorPlaneUtils.handleAmountsPlane(buffer, this.worldMatrix, this.camera, this.pathPoints, this.pathCoreColors, planeLimit, max7, 1.25F, false, true);
                     return;
                  }
               }

               double deltaVar = cameraLimit3 - min6;
               double cameraLimit4 = cameraLimit3 + (deltaVar < 4.0 * this.maxRenderDistance ? clampDouble(deltaVar / 24.0, 0.5, 64.0) : deltaVar / 24.0);

               while (index < cameraLimit && this.pathCumulativeLengths[index] <= cameraLimit3 + 1.0E-6) {
                  index++;
               }

               if (index < cameraLimit && this.pathCumulativeLengths[index] < cameraLimit4) {
                  cameraLimit4 = this.pathCumulativeLengths[index];
               }

               if (cameraLimit4 > cameraBound || planeLimit >= 1023) {
                  cameraLimit4 = cameraBound;
               }

               cameraLimit3 = cameraLimit4;
            }
         }
      }
   }

   private void renderWorldOverlay(QueueCommand queueCommand, PilotRender.Scene scene) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.world != null && instance.gameRenderer != null) {
         long nanoTime = System.nanoTime();
         float cameraLerpValue = this.lastNanoTime == 0L ? 0.0F : Math.min(0.1F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
         this.lastNanoTime = nanoTime;
         boolean cameraLerpFlag = scene.destinationActive && isFiniteDouble(scene.destinationX) && isFiniteDouble(scene.destinationZ);
         this.worldFadeProgress = cameraLerpFlag ? Math.min(1.0F, this.worldFadeProgress + cameraLerpValue / 0.26F) : Math.max(0.0F, this.worldFadeProgress - cameraLerpValue / 0.32F);
         if (!(this.worldFadeProgress <= 0.0F)) {
            float amountForFloat = MatrixAmountUtils.getAmountForFloat(this.worldFadeProgress);
            this.camera = instance.gameRenderer.getCamera();
            if (this.camera != null) {
               Vec3d cameraPos = this.camera.getCameraPos();
               this.cameraX = cameraPos.x;
               this.cameraY = cameraPos.y;
               this.cameraZ = cameraPos.z;
               this.worldMatrix = VertexBuilderUtils.getMatrix();
               this.framebufferHeight = VectorPlaneUtils.getFramebufferHeight();
               if (this.framebufferHeight > 0.001F) {
                  this.resolutionScale = instance.getWindow().getFramebufferHeight() / 1080.0F;
                  this.lineWidthScale = Math.max(1.0F, this.resolutionScale);
                  this.maxRenderDistance = getMaxRenderDistance(instance);
                  this.accentColor = 0xFF000000 | MatrixEspUtils.releaseRightValue;
                  this.accentTintColor = CardMatrixUtils.getValue(this.accentColor, -1, 0.55);
                  double delta = queueCommand == null ? 1.0 : queueCommand.tickProgress;
                  double lerp = MathHelper.lerp(delta, player.lastX, player.getX());
                  double lerp2 = MathHelper.lerp(delta, player.lastY, player.getY());
                  double lerp3 = MathHelper.lerp(delta, player.lastZ, player.getZ());
                  double seconds = nanoTime / 1.0E9;
                  double sin = Math.sin(seconds * Math.PI * 2.0 / 2.6);
                  int amountsDestination = this.collectRoutePoints(scene, lerp, lerp2, lerp3);
                  if (amountsDestination >= 2) {
                     this.renderRouteLine(amountsDestination, lerp, lerp2, lerp3, amountForFloat, seconds);
                  }

                  if (isFiniteDouble(scene.destinationX) && isFiniteDouble(scene.destinationZ)) {
                     this.renderDestinationBeam(scene, amountForFloat, sin);
                     this.renderDestinationRing(scene, amountForFloat, sin, seconds);
                  }
               }
            }
         }
      }
   }

   private static double getDestinationY(PilotRender.Scene scene) {
      if (isFiniteDouble(scene.destinationY)) {
         return scene.destinationY;
      } else {
         int destinationLimit = Math.min(scene.routePointCount, scene.routePoints.length / 3);
         if (destinationLimit > 0) {
            double destinationAmounts = scene.routePoints[(destinationLimit - 1) * 3 + 1];
            if (isFiniteDouble(destinationAmounts)) {
               return destinationAmounts;
            }
         }

         return 62.0;
      }
   }

   private static float smoothstepForDouble(double pilotRenderValue) {
      return MatrixAmountUtils.getAmountForFloat((float)pilotRenderValue);
   }

   public static final class Scene {
      public double destinationZ;
      public String destinationLabel;
      public double destinationX;
      public long etaSeconds;
      public boolean highlightDestination;
      public double distanceToDestination;
      public boolean destinationActive;
      public double totalRouteDistance;
      public int routePointCount;
      public final double[] routePoints;
      public double destinationY = Double.NaN;

      public Scene() {
         this.routePoints = new double[192];
         this.etaSeconds = -1L;
         this.destinationLabel = "Destination";
      }
   }
}
