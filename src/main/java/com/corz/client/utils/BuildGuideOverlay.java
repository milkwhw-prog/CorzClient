package com.corz.client.utils;

import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.VectorPlaneUtils;
import com.corz.client.render.VertexBuilderUtils;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import com.corz.client.utils.PolylinePathMath;
import com.corz.client.utils.VoxelSurfaceMesher;

public final class BuildGuideOverlay {
   private final BuildGuideOverlay.TrailSlot[] trailSlots;
   private double[] pathPoints;
   private long cutMissingStartNanos;
   private float framebufferHeightPx;
   private long lastRenderNanos;
   private int lightAccentColor;
   private long trailActiveStartNanos;
   private long cellMissingStartNanos;
   private float resolutionScale;
   private final double[] trailTangentBuffer;
   private final double[] scratchPoints;
   private long[] targetPosKeys;
   private boolean disabledAfterError;
   private final BuildGuideOverlay.AreaSlot[] areaSlots = new BuildGuideOverlay.AreaSlot[]{
      new BuildGuideOverlay.AreaSlot(), new BuildGuideOverlay.AreaSlot(), new BuildGuideOverlay.AreaSlot()
   };
   private final double[] ringPoints;
   private int[] trailGlowColors;
   private final BuildGuideOverlay.CellSlot[] cellSlots;
   private double cameraZ;
   private int accentColor;
   private final BuildGuideOverlay.Frame frame;
   private final BuildGuideOverlay.CutSlot[] cutSlots = new BuildGuideOverlay.CutSlot[]{
      new BuildGuideOverlay.CutSlot(), new BuildGuideOverlay.CutSlot(), new BuildGuideOverlay.CutSlot(), new BuildGuideOverlay.CutSlot(), new BuildGuideOverlay.CutSlot()
   };
   private long trailLostStartNanos;
   private final double[] trailPointBuffer;
   private Matrix4f positionMatrix;
   private int[] trailCoreColors;
   private final int[] ringColors;
   private Camera camera;
   private double cameraX;
   private float fadeProgress;
   private double cameraY;
   private int[] targetCoords;
   private double[] pathCumulativeDistances;
   private int[] trailHaloColors;
   private final int[] lineColors;
   private boolean trailActive;

   private void drawLineSegment(VertexConsumer vertices, double cameraValue, double cameraValue2, double lowZ, double cameraValue4, double lowY, double cameraValue6, int posBlockValue, int cameraValue8, float planeValue) {
      if ((posBlockValue | cameraValue8) >>> 24 != 0) {
         this.scratchPoints[0] = cameraValue;
         this.scratchPoints[1] = cameraValue2;
         this.scratchPoints[2] = lowZ;
         this.scratchPoints[3] = cameraValue4;
         this.scratchPoints[4] = lowY;
         this.scratchPoints[5] = cameraValue6;
         this.lineColors[0] = posBlockValue;
         this.lineColors[1] = cameraValue8;
         VectorPlaneUtils.handleAmountsPlane(vertices, this.positionMatrix, this.camera, this.scratchPoints, this.lineColors, 2, planeValue, 1.0F, false, false);
      }
   }

   private static <T extends BuildGuideOverlay.Slot> T acquireSlot(T[] ts) {
      for (BuildGuideOverlay.Slot slot : ts) {
         if (!slot.slotActive) {
            return (T)slot;
         }
      }

      BuildGuideOverlay.Slot slot2 = ts[0];

      for (BuildGuideOverlay.Slot slot3 : ts) {
         if (slot3.fadeStartNanos != 0L && (slot2.fadeStartNanos == 0L || slot3.fadeStartNanos < slot2.fadeStartNanos)) {
            slot2 = slot3;
         }
      }

      return (T)slot2;
   }

   private int buildStraightPath(BuildGuideOverlay.TrailSlot trailSlot, double posBlockValue, double posBlockValue2, double posBlockValue3) {
      double posBlockFactor = trailSlot.endX - posBlockValue;
      double posBlockFactor2 = trailSlot.endY - posBlockValue2;
      double posBlockFactor3 = trailSlot.endZ - posBlockValue3;
      double distance = Math.sqrt(posBlockFactor * posBlockFactor + posBlockFactor2 * posBlockFactor2 + posBlockFactor3 * posBlockFactor3);
      int posBlockLimit = Math.max(1, Math.min(400, (int)Math.ceil(distance / 0.25)));
      this.ensurePathCapacity(posBlockLimit + 1);

      for (int i = 0; i <= posBlockLimit; i++) {
         double posBlockFactor4 = (double)i / posBlockLimit;
         this.pathPoints[i * 3] = posBlockValue + posBlockFactor * posBlockFactor4;
         this.pathPoints[i * 3 + 1] = posBlockValue2 + posBlockFactor2 * posBlockFactor4;
         this.pathPoints[i * 3 + 2] = posBlockValue3 + posBlockFactor3 * posBlockFactor4;
         this.pathCumulativeDistances[i] = distance * posBlockFactor4;
      }

      return posBlockLimit + 1;
   }

   private void renderTrail(BuildGuideOverlay.TrailSlot trailSlot, float cameraFactor, long cameraValue, double posBlockValue, double cameraValue3, double cameraValue4) {
      double cameraFactor2 = trailSlot.endX - posBlockValue;
      double cameraFactor3 = trailSlot.endZ - cameraValue4;
      if (!(cameraFactor2 * cameraFactor2 + cameraFactor3 * cameraFactor3 > 36864.0)) {
         int cameraLimit = trailSlot.straightLine ? this.buildStraightPath(trailSlot, posBlockValue, cameraValue3, cameraValue4) : this.buildCurvedPath(trailSlot, posBlockValue, cameraValue3, cameraValue4);
         if (cameraLimit >= 2) {
            double cameraDivisor = this.pathCumulativeDistances[cameraLimit - 1];
            if (cameraDivisor > 0.05) {
               float posBlockFactor = 0.5F * (3.0F * this.resolutionScale + 1.0F);

               for (int i = 0; i < cameraLimit; i++) {
                  double cameraValue6 = this.pathCumulativeDistances[i];
                  double deltaVar = cameraDivisor - cameraValue6;
                  float cameraFactor4 = MatrixAmountUtils.getAmountForFloat((float)(cameraValue6 / 0.6))
                     * (1.0F - 0.55F * MatrixAmountUtils.getAmountForFloat((float)(cameraValue6 / cameraDivisor)))
                     * (0.3F + 0.7F * MatrixAmountUtils.getAmountForFloat((float)(deltaVar / 0.5)));
                  float cameraValue7 = cameraFactor * cameraFactor4;
                  this.trailCoreColors[i] = MatrixAmountUtils.getValueInt(this.accentColor, 0.92F * cameraValue7);
                  this.trailHaloColors[i] = MatrixAmountUtils.getValueInt(this.accentColor, 0.11F * cameraValue7);
                  this.trailGlowColors[i] = MatrixAmountUtils.getValueInt(this.accentColor, 0.3F * cameraValue7);
                  this.pathPoints[i * 3 + 1] = this.pathPoints[i * 3 + 1]
                     + this.computeLiftAbovePath(this.pathPoints[i * 3], this.pathPoints[i * 3 + 1], this.pathPoints[i * 3 + 2], posBlockFactor);
               }

               float planeValue = 3.0F * this.resolutionScale;
               VectorPlaneUtils.handleAmountsPlane(
                  VertexBuilderUtils.getBufferIMMEDIATE(), this.positionMatrix, this.camera, this.pathPoints, this.trailGlowColors, cameraLimit, planeValue, 1.0F, false, false
               );
               VertexConsumer buffer = VertexBuilderUtils.getBuffer();
               VectorPlaneUtils.handleAmountsPlane(
                  buffer, this.positionMatrix, this.camera, this.pathPoints, this.trailHaloColors, cameraLimit, 9.0F * this.resolutionScale, 9.0F * this.resolutionScale, false, false
               );
               VectorPlaneUtils.handleAmountsPlane(buffer, this.positionMatrix, this.camera, this.pathPoints, this.trailCoreColors, cameraLimit, planeValue, 1.0F, false, false);
               this.renderTrailChevrons(cameraLimit, cameraDivisor, cameraFactor, cameraValue);
               this.renderTrailEndRing(trailSlot, cameraFactor, cameraValue);
            }
         }
      }
   }

   private void addQuadZPlane(VertexConsumer vertices, double matrixValue, double vertexBuilderValue, double matrixValue3, double matrixValue4, double matrixValue5, int valueInt) {
      float amountForDouble = VertexBuilderUtils.getAmountForDouble(matrixValue);
      float amountX = VertexBuilderUtils.getAmountX(vertexBuilderValue);
      float amountX9 = VertexBuilderUtils.getAmountX(matrixValue4);
      float deltaY = VertexBuilderUtils.getAmount(matrixValue3);
      float matrixValue11 = VertexBuilderUtils.getAmount(matrixValue5);
      VertexBuilderUtils.handleVertexColor(vertices, this.positionMatrix, amountX, deltaY, amountForDouble, valueInt, amountX9, deltaY, amountForDouble, valueInt, amountX9, matrixValue11, amountForDouble, valueInt, amountX, matrixValue11, amountForDouble, valueInt);
   }

   private void renderGuides(boolean cameraLerpFlag, float cameraLerpValue) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.world != null && instance.gameRenderer != null) {
         long nanoTime = System.nanoTime();
         float cameraLerpValue2 = this.lastRenderNanos == 0L ? 0.0F : Math.min(100.0F, (float)(nanoTime - this.lastRenderNanos) / 1000000.0F);
         this.lastRenderNanos = nanoTime;
         this.fadeProgress = cameraLerpFlag ? Math.min(1.0F, this.fadeProgress + cameraLerpValue2 / 260.0F) : Math.max(0.0F, this.fadeProgress - cameraLerpValue2 / 320.0F);
         if (this.fadeProgress <= 0.0F) {
            if (!cameraLerpFlag) {
               this.resetAllSlots();
            }
         } else {
            float cameraLerpFactor = MatrixAmountUtils.getAmountForFloat(this.fadeProgress);
            this.camera = instance.gameRenderer.getCamera();
            if (this.camera != null) {
               Vec3d cameraPos = this.camera.getCameraPos();
               this.cameraX = cameraPos.x;
               this.cameraY = cameraPos.y;
               this.cameraZ = cameraPos.z;
               this.positionMatrix = VertexBuilderUtils.getMatrix();
               this.framebufferHeightPx = VectorPlaneUtils.getFramebufferHeight();
               if (this.framebufferHeightPx > 0.001F) {
                  this.resolutionScale = Math.max(1.0F, instance.getWindow().getFramebufferHeight() / 1080.0F);
                  this.accentColor = 0xFF000000 | MatrixEspUtils.releaseRightValue;
                  this.lightAccentColor = CardMatrixUtils.getValue(this.accentColor, -1, 0.55);
                  double lerp = MathHelper.lerp((double)cameraLerpValue, player.lastX, player.getX());
                  double lerp2 = MathHelper.lerp((double)cameraLerpValue, player.lastY, player.getY());
                  double lerp3 = MathHelper.lerp((double)cameraLerpValue, player.lastZ, player.getZ());
                  if (cameraLerpFlag) {
                     this.updateSlots(nanoTime, lerp, lerp2, lerp3);
                  }

                  for (BuildGuideOverlay.AreaSlot areaSlot : this.areaSlots) {
                     float cameraLerpLimit = areaSlot.getFadeAlpha(nanoTime, 220000000L, 300000000L) * cameraLerpFactor;
                     if (cameraLerpLimit > 0.003F) {
                        this.renderAreaSlot(areaSlot, cameraLerpLimit);
                     }
                  }

                  for (BuildGuideOverlay.CutSlot cutSlot : this.cutSlots) {
                     float cameraLerpLimit2 = cutSlot.getFadeAlpha(nanoTime, 220000000L, 300000000L) * cameraLerpFactor;
                     if (cameraLerpLimit2 > 0.003F) {
                        this.renderCutSlot(cutSlot, cameraLerpLimit2, nanoTime);
                     }
                  }

                  float posBlockValue = 0.5F + 0.5F * (float)Math.sin(nanoTime / 1.0E9 * Math.PI * 2.0 / 1.1);

                  for (BuildGuideOverlay.CellSlot cellSlot : this.cellSlots) {
                     float posBlockFactor = cellSlot.getFadeAlpha(nanoTime, 220000000L, 300000000L) * cameraLerpFactor;
                     if (posBlockFactor > 0.003F) {
                        this.renderCellSlot(cellSlot, posBlockFactor, posBlockValue);
                     }
                  }

                  for (BuildGuideOverlay.TrailSlot trailSlot : this.trailSlots) {
                     float cameraFactor = trailSlot.getFadeAlpha(nanoTime, 240000000L, 260000000L) * cameraLerpFactor;
                     if (cameraFactor > 0.003F) {
                        this.renderTrail(trailSlot, cameraFactor, nanoTime, lerp, lerp2, lerp3);
                     }
                  }
               }
            }
         }
      }
   }

   private int buildCurvedPath(BuildGuideOverlay.TrailSlot trailSlot, double posBlockFactor, double posBlockFactor2, double posBlockFactor3) {
      double posBlockValue = trailSlot.cumulativeLengths[trailSlot.pointCount - 1];
      double distanceAlongPath = PolylinePathMath.getDistanceAlongPath(trailSlot.routePoints, trailSlot.cumulativeLengths, trailSlot.pointCount, posBlockFactor, posBlockFactor2, posBlockFactor3);
      double min4 = Math.min(0.9, Math.max(0.0, posBlockValue - distanceAlongPath));
      double pilotValue = distanceAlongPath + min4;
      int posBlockValue4 = PolylinePathMath.findSegmentIndex(trailSlot.cumulativeLengths, trailSlot.pointCount, pilotValue) + 1;
      this.ensurePathCapacity(8 + (trailSlot.pointCount - posBlockValue4) + 1);
      PolylinePathMath.samplePositionAtDistance(trailSlot.routePoints, trailSlot.cumulativeLengths, trailSlot.pointCount, pilotValue, this.trailPointBuffer);
      PolylinePathMath.sampleDirectionAtDistance(trailSlot.routePoints, trailSlot.cumulativeLengths, trailSlot.pointCount, pilotValue, this.trailTangentBuffer);
      double posBlockFactor5 = this.trailPointBuffer[0];
      double posBlockFactor6 = this.trailPointBuffer[1];
      double posBlockFactor7 = this.trailPointBuffer[2];
      double posBlockFactor8 = posBlockFactor5 - this.trailTangentBuffer[0] * min4 * 0.55;
      double posBlockFactor9 = posBlockFactor6 - this.trailTangentBuffer[1] * min4 * 0.55;
      double posBlockFactor10 = posBlockFactor7 - this.trailTangentBuffer[2] * min4 * 0.55;
      int index = 0;
      double posBlockTotal = 0.0;

      for (int i = 0; i <= 7; i++) {
         double posBlockFactor11 = i / 7.0;
         double posBlockFactor12 = 1.0 - posBlockFactor11;
         double posBlockValue5 = posBlockFactor12 * posBlockFactor12 * posBlockFactor + 2.0 * posBlockFactor12 * posBlockFactor11 * posBlockFactor8 + posBlockFactor11 * posBlockFactor11 * posBlockFactor5;
         double posBlockValue6 = posBlockFactor12 * posBlockFactor12 * posBlockFactor2 + 2.0 * posBlockFactor12 * posBlockFactor11 * posBlockFactor9 + posBlockFactor11 * posBlockFactor11 * posBlockFactor6;
         double posBlockValue7 = posBlockFactor12 * posBlockFactor12 * posBlockFactor3 + 2.0 * posBlockFactor12 * posBlockFactor11 * posBlockFactor10 + posBlockFactor11 * posBlockFactor11 * posBlockFactor7;
         if (index > 0) {
            posBlockTotal += Math.sqrt(
               square(posBlockValue5 - this.pathPoints[index * 3 - 3]) + square(posBlockValue6 - this.pathPoints[index * 3 - 2]) + square(posBlockValue7 - this.pathPoints[index * 3 - 1])
            );
         }

         this.pathPoints[index * 3] = posBlockValue5;
         this.pathPoints[index * 3 + 1] = posBlockValue6;
         this.pathPoints[index * 3 + 2] = posBlockValue7;
         this.pathCumulativeDistances[index] = posBlockTotal;
         index++;
      }

      for (int j = posBlockValue4; j < trailSlot.pointCount; j++) {
         if (!(trailSlot.cumulativeLengths[j] <= pilotValue + 1.0E-6)) {
            double posBlockValue8 = trailSlot.routePoints[j * 3];
            double posBlockValue9 = trailSlot.routePoints[j * 3 + 1];
            double posBlockValue10 = trailSlot.routePoints[j * 3 + 2];
            posBlockTotal += Math.sqrt(
               square(posBlockValue8 - this.pathPoints[index * 3 - 3]) + square(posBlockValue9 - this.pathPoints[index * 3 - 2]) + square(posBlockValue10 - this.pathPoints[index * 3 - 1])
            );
            this.pathPoints[index * 3] = posBlockValue8;
            this.pathPoints[index * 3 + 1] = posBlockValue9;
            this.pathPoints[index * 3 + 2] = posBlockValue10;
            this.pathCumulativeDistances[index] = posBlockTotal;
            index++;
         }
      }

      return index;
   }

   private void addQuadXPlane(VertexConsumer vertices, double matrixValue, double vertexBuilderValue, double matrixValue3, double matrixValue4, double matrixValue5, int valueInt) {
      float amountX = VertexBuilderUtils.getAmountX(matrixValue);
      float matrixValue8 = VertexBuilderUtils.getAmount(vertexBuilderValue);
      float deltaY = VertexBuilderUtils.getAmount(matrixValue4);
      float amountForDouble = VertexBuilderUtils.getAmountForDouble(matrixValue3);
      float amountForDouble11 = VertexBuilderUtils.getAmountForDouble(matrixValue5);
      VertexBuilderUtils.handleVertexColor(
         vertices, this.positionMatrix, amountX, matrixValue8, amountForDouble, valueInt, amountX, deltaY, amountForDouble, valueInt, amountX, deltaY, amountForDouble11, valueInt, amountX, matrixValue8, amountForDouble11, valueInt
      );
   }

   private void addQuadYPlane(VertexConsumer vertices, double matrixValue, double vertexBuilderValue, double matrixValue3, double matrixValue4, double matrixValue5, int valueInt) {
      float deltaY = VertexBuilderUtils.getAmount(matrixValue);
      float amountX = VertexBuilderUtils.getAmountX(vertexBuilderValue);
      float amountX9 = VertexBuilderUtils.getAmountX(matrixValue4);
      float amountForDouble = VertexBuilderUtils.getAmountForDouble(matrixValue3);
      float amountForDouble11 = VertexBuilderUtils.getAmountForDouble(matrixValue5);
      VertexBuilderUtils.handleVertexColor(vertices, this.positionMatrix, amountX, deltaY, amountForDouble, valueInt, amountX9, deltaY, amountForDouble, valueInt, amountX9, deltaY, amountForDouble11, valueInt, amountX, deltaY, amountForDouble11, valueInt);
   }

   private void renderAreaSlot(BuildGuideOverlay.AreaSlot areaSlot, float posBlockValue) {
      double lowX = areaSlot.lowX;
      double lowY = areaSlot.lowY;
      double lowZ = areaSlot.lowZ;
      double posBlockValue2 = areaSlot.highX + 1.0;
      double posBlockValue3 = areaSlot.highY + 1.0;
      double posBlockValue4 = areaSlot.highZ + 1.0;
      if (!(this.distanceToBox(lowX, lowY, lowZ, posBlockValue2, posBlockValue3, posBlockValue4) > 576.0)) {
         int valueInt = MatrixAmountUtils.getValueInt(-5854026, 0.32F * posBlockValue);
         int valueInt6 = MatrixAmountUtils.getValueInt(-920587, 0.9F * posBlockValue);
         float posBlockLimit = MatrixAmountUtils.getAmountForFloat((float)((this.distanceToBox(lowX, lowY, lowZ, posBlockValue2, posBlockValue3, posBlockValue4) - 0.25) / 2.5));
         if (posBlockLimit > 0.01F) {
            this.drawBoxFaces(lowX, lowY, lowZ, posBlockValue2, posBlockValue3, posBlockValue4, MatrixAmountUtils.getValueInt(this.accentColor, 0.025F * posBlockValue * posBlockLimit), 0.003);
         }

         VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();
         double cornerLength = computeCornerLength(posBlockValue2 - lowX);
         double posBlockBound = computeCornerLength(posBlockValue3 - lowY);
         double cornerLength9 = computeCornerLength(posBlockValue4 - lowZ);

         for (int i = 0; i < 4; i++) {
            double posBlockValue10 = (i & 1) == 0 ? lowY : posBlockValue3;
            double posBlockValue11 = (i & 2) == 0 ? lowZ : posBlockValue4;
            this.drawEdgeWithCorners(buffer, lowX, posBlockValue10, posBlockValue11, posBlockValue2, posBlockValue10, posBlockValue11, valueInt, valueInt6, cornerLength, true, true);
            double posBlockValue12 = (i & 1) == 0 ? lowX : posBlockValue2;
            this.drawEdgeWithCorners(buffer, posBlockValue12, lowY, posBlockValue11, posBlockValue12, posBlockValue3, posBlockValue11, valueInt, valueInt6, posBlockBound, true, true);
            double posBlockValue13 = (i & 2) == 0 ? lowY : posBlockValue3;
            this.drawEdgeWithCorners(buffer, posBlockValue12, posBlockValue13, lowZ, posBlockValue12, posBlockValue13, posBlockValue4, valueInt, valueInt6, cornerLength9, true, true);
         }
      }
   }

   private BuildGuideOverlay.AreaSlot getActiveAreaSlot() {
      for (BuildGuideOverlay.AreaSlot areaSlot : this.areaSlots) {
         if (areaSlot.isActiveAndNotFading()) {
            return areaSlot;
         }
      }

      return null;
   }

   private static double computeCornerLength(double posBlockValue) {
      return Math.min(posBlockValue * 0.42, Math.max(0.45, Math.min(1.6, posBlockValue * 0.12)));
   }

   private void updateTrailSlot(long nanoTime, double posValue, double posValue2, double posValue3) {
      BuildGuideOverlay.Frame frame3 = this.frame;
      boolean posFlag = frame3.trailEnabled && frame3.targetBlock != null && frame3.targetActive && isPlayerAwayFromBlock(frame3.targetBlock, posValue, posValue2, posValue3);
      if (posFlag) {
         if (this.trailActiveStartNanos == 0L) {
            this.trailActiveStartNanos = nanoTime;
         }

         this.trailLostStartNanos = 0L;
      } else {
         if (this.trailLostStartNanos == 0L) {
            this.trailLostStartNanos = nanoTime;
         }

         this.trailActiveStartNanos = 0L;
      }

      this.trailActive = this.trailActive ? this.trailLostStartNanos == 0L || nanoTime - this.trailLostStartNanos <= 200000000L : this.trailActiveStartNanos != 0L && nanoTime - this.trailActiveStartNanos > 110000000L;
      BuildGuideOverlay.TrailSlot activeTrailSlot = this.getActiveTrailSlot();
      if (!this.trailActive) {
         if (activeTrailSlot != null) {
            activeTrailSlot.startFadeOut(nanoTime);
         }
      } else if (frame3.targetBlock != null) {
         boolean posBlockFlag = frame3.pathPositions != null && !frame3.pathPositions.isEmpty();
         if (activeTrailSlot != null) {
            boolean posFlag3 = activeTrailSlot.targetX == frame3.targetBlock.getX() && activeTrailSlot.targetY == frame3.targetBlock.getY() && activeTrailSlot.targetZ == frame3.targetBlock.getZ();
            if (activeTrailSlot.routeSource == frame3.pathPositions && posFlag3) {
               return;
            }

            if (posFlag3 && !posBlockFlag) {
               activeTrailSlot.routeSource = frame3.pathPositions;
               return;
            }

            if (posFlag3 && !activeTrailSlot.straightLine) {
               int times = this.fillPosKeys(frame3.pathPositions);
               if (PolylinePathMath.endsWithSequence(activeTrailSlot.routeKeys, activeTrailSlot.routeKeyCount, this.targetPosKeys, times)) {
                  activeTrailSlot.routeSource = frame3.pathPositions;
                  return;
               }
            }

            activeTrailSlot.startFadeOut(nanoTime);
         }

         BuildGuideOverlay.TrailSlot timeFlag = acquireSlot(this.trailSlots);
         this.assignTrailTarget(timeFlag, frame3, posBlockFlag, posValue, posValue2, posValue3);
         timeFlag.activate(nanoTime);
      }
   }

   private void renderTrailEndRing(BuildGuideOverlay.TrailSlot trailSlot, float matrixValue, long cameraValue) {
      double seconds = cameraValue / 1.0E9;
      double sin = Math.sin(seconds * Math.PI * 2.0 / 2.6);
      double matrixFactor = 0.31 + 0.03 * sin;
      float matrixFactor2 = 0.72F + 0.28F * (float)(0.5 + 0.5 * sin);
      double endX = trailSlot.endX;
      double endZ = trailSlot.endZ;
      double endY = trailSlot.endY;
      if (!VertexBuilderUtils.canVector(endX, endY, endZ, 1.0F)) {
         float planeValue = 1.6F * this.resolutionScale;
         float posBlockFactor = 0.5F * (planeValue + 1.0F);
         double matrixValue6 = endY + this.computeLiftAbovePath(endX, endY, endZ, posBlockFactor);

         for (int i = 0; i <= 48; i++) {
            double angle = (Math.PI * 2) * i / 48.0;
            this.ringPoints[i * 3] = endX + Math.cos(angle) * matrixFactor;
            this.ringPoints[i * 3 + 1] = matrixValue6;
            this.ringPoints[i * 3 + 2] = endZ + Math.sin(angle) * matrixFactor;
         }

         int valueInt = MatrixAmountUtils.getValueInt(this.accentColor, 0.3F * matrixValue * matrixFactor2);

         for (int j = 0; j <= 48; j++) {
            this.ringColors[j] = valueInt;
         }

         VectorPlaneUtils.handleAmountsPlane(
            VertexBuilderUtils.getBufferIMMEDIATE(), this.positionMatrix, this.camera, this.ringPoints, this.ringColors, 49, planeValue, 1.0F, false, false
         );
         int valueInt8 = MatrixAmountUtils.getValueInt(this.lightAccentColor, 0.95F * matrixValue * matrixFactor2);

         for (int k = 0; k <= 48; k++) {
            this.ringColors[k] = valueInt8;
         }

         VertexConsumer buffer = VertexBuilderUtils.getBuffer();
         VectorPlaneUtils.handleAmountsPlane(buffer, this.positionMatrix, this.camera, this.ringPoints, this.ringColors, 49, planeValue, 1.0F, false, false);
         int valueInt9 = MatrixAmountUtils.getValueInt(this.accentColor, 0.14F * matrixValue * matrixFactor2);
         int valueInt10 = MatrixAmountUtils.getValueInt(this.accentColor, 0.03F * matrixValue);
         float amountX = VertexBuilderUtils.getAmountX(endX);
         float deltaY = VertexBuilderUtils.getAmount(matrixValue6 - 0.004);
         float amountForDouble = VertexBuilderUtils.getAmountForDouble(endZ);

         for (int m = 0; m < 48; m++) {
            int index = m * 3;
            int index2 = (m + 1) * 3;
            VertexBuilderUtils.handleVertexColor(
               buffer,
               this.positionMatrix,
               amountX,
               deltaY,
               amountForDouble,
               valueInt10,
               VertexBuilderUtils.getAmountX(this.ringPoints[index]),
               deltaY,
               VertexBuilderUtils.getAmountForDouble(this.ringPoints[index + 2]),
               valueInt9,
               VertexBuilderUtils.getAmountX(this.ringPoints[index2]),
               deltaY,
               VertexBuilderUtils.getAmountForDouble(this.ringPoints[index2 + 2]),
               valueInt9,
               amountX,
               deltaY,
               amountForDouble,
               valueInt10
            );
         }
      }
   }

   private static boolean canReuseCutSlot(BuildGuideOverlay.CutSlot cutSlot, BuildGuideOverlay.Frame frame) {
      int posBlockValue = Math.min(frame.startX, frame.endX);
      int min2 = Math.min(frame.endY, frame.startY);
      int min3 = Math.min(frame.startZ, frame.endZ);
      int max4 = Math.max(frame.startX, frame.endX);
      int max5 = Math.max(frame.endY, frame.startY);
      int max6 = Math.max(frame.startZ, frame.endZ);
      return max4 - posBlockValue == cutSlot.maxX - cutSlot.minX && max5 - min2 == cutSlot.maxY - cutSlot.minY && max6 - min3 == cutSlot.maxZ - cutSlot.minZ
         ? Math.abs(posBlockValue - cutSlot.minX) <= 4 && Math.abs(min2 - cutSlot.minY) <= 4 && Math.abs(min3 - cutSlot.minZ) <= 4
         : false;
   }

   private static int scaleAlpha(int posBlockValue, float posBlockBound) {
      int round = Math.round((posBlockValue >>> 24) * Math.max(0.0F, Math.min(1.0F, posBlockBound)));
      return round << 24 | posBlockValue & 16777215;
   }

   private void resetAllSlots() {
      for (BuildGuideOverlay.AreaSlot areaSlot : this.areaSlots) {
         areaSlot.slotActive = false;
      }

      for (BuildGuideOverlay.CutSlot cutSlot : this.cutSlots) {
         cutSlot.slotActive = false;
      }

      for (BuildGuideOverlay.CellSlot cellSlot : this.cellSlots) {
         cellSlot.slotActive = false;
      }

      for (BuildGuideOverlay.TrailSlot trailSlot : this.trailSlots) {
         trailSlot.slotActive = false;
      }

      this.trailActive = false;
      this.trailActiveStartNanos = this.trailLostStartNanos = this.cutMissingStartNanos = this.cellMissingStartNanos = 0L;
   }

   private double computeLiftAbovePath(double posBlockValue, double endY, double endZ, float posBlockFactor) {
      double sqrt = Math.sqrt(square(posBlockValue - this.cameraX) + square(endY - this.cameraY) + square(endZ - this.cameraZ));
      return 0.045 + posBlockFactor * sqrt / this.framebufferHeightPx;
   }

   private void renderCutSlot(BuildGuideOverlay.CutSlot cutSlot, float posBlockValue, long cutSlotValue) {
      float posBlockFactor = cutSlot.getAnimationFactor(cutSlotValue);
      double posBlockValue3 = cutSlot.offsetX * posBlockFactor;
      double posBlockValue4 = cutSlot.offsetY * posBlockFactor;
      double posBlockValue5 = cutSlot.offsetZ * posBlockFactor;
      double posBlockValue6 = cutSlot.minX + posBlockValue3;
      double lowY = cutSlot.minY + posBlockValue4;
      double posBlockValue8 = cutSlot.minZ + posBlockValue5;
      double posBlockValue9 = cutSlot.maxX + 1.0 + posBlockValue3;
      double posBlockValue10 = cutSlot.maxY + 1.0 + posBlockValue4;
      double posBlockValue11 = cutSlot.maxZ + 1.0 + posBlockValue5;
      double posBlockLimit = this.distanceToBox(posBlockValue6, lowY, posBlockValue8, posBlockValue9, posBlockValue10, posBlockValue11);
      if (!(posBlockLimit > 192.0)) {
         float amountForFloat = MatrixAmountUtils.getAmountForFloat((float)((posBlockLimit - 0.15) / 1.2));
         if (amountForFloat > 0.01F) {
            int valueInt = MatrixAmountUtils.getValueInt(this.accentColor, 0.045F * posBlockValue * amountForFloat);

            for (int i = 0; i + 3 < cutSlot.fillFaces.length; i += 4) {
               this.drawFaceQuad(cutSlot.fillFaces[i] + posBlockValue3, cutSlot.fillFaces[i + 1] + posBlockValue4, cutSlot.fillFaces[i + 2] + posBlockValue5, cutSlot.fillFaces[i + 3], valueInt, 0.004);
            }
         }

         VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();
         float posBlockFactor2 = 0.3F + 0.7F * MatrixAmountUtils.getAmountForFloat((float)((posBlockLimit - 0.25) / 1.75));
         int valueInt13 = MatrixAmountUtils.getValueInt(this.accentColor, 0.82F * posBlockValue * posBlockFactor2);
         int valueInt14 = MatrixAmountUtils.getValueInt(this.lightAccentColor, 1.0F * posBlockValue * posBlockFactor2);
         int[] outlineEdges = cutSlot.outlineEdges;

         for (int j = 0; j + 6 < outlineEdges.length; j += 7) {
            int posBlockValue15 = outlineEdges[j];
            int posBlockValue16 = outlineEdges[j + 4];
            double posBlockValue17 = outlineEdges[j + 1] + posBlockValue3;
            double posBlockValue18 = outlineEdges[j + 2] + posBlockValue4;
            double lowZ = outlineEdges[j + 3] + posBlockValue5;
            double posBlockValue20 = posBlockValue17 + (posBlockValue15 == 0 ? posBlockValue16 : 0);
            double posBlockValue21 = posBlockValue18 + (posBlockValue15 == 1 ? posBlockValue16 : 0);
            double posBlockValue22 = lowZ + (posBlockValue15 == 2 ? posBlockValue16 : 0);
            this.drawEdgeWithCorners(buffer, posBlockValue17, posBlockValue18, lowZ, posBlockValue20, posBlockValue21, posBlockValue22, valueInt13, valueInt14, 0.24, outlineEdges[j + 5] != 0, outlineEdges[j + 6] != 0);
         }

         if (cutSlot.growAxis >= 0) {
            int[] posBlockValues = cutSlot.extrudeEdges;

            for (int k = 0; k + 6 < posBlockValues.length; k += 7) {
               int posBlockValue23 = posBlockValues[k];
               int posBlockValue24 = posBlockValues[k + 4];
               double posBlockValue25 = posBlockValues[k + 1];
               double posBlockValue26 = posBlockValues[k + 2];
               double posBlockValue27 = posBlockValues[k + 3];
               double posBlockValue28 = posBlockValue25 + (posBlockValue23 == 0 ? posBlockValue24 : 0);
               double posBlockValue29 = posBlockValue26 + (posBlockValue23 == 1 ? posBlockValue24 : 0);
               double posBlockValue30 = posBlockValue27 + (posBlockValue23 == 2 ? posBlockValue24 : 0);
               float extrudeFade = getExtrudeFade(cutSlot, posBlockValue25, posBlockValue26, posBlockValue27);
               float extrudeFade4 = getExtrudeFade(cutSlot, posBlockValue28, posBlockValue29, posBlockValue30);
               double posBlockValue31 = posBlockValue25 + posBlockValue3;
               double posBlockValue32 = posBlockValue26 + posBlockValue4;
               double posBlockValue33 = posBlockValue27 + posBlockValue5;
               double posBlockValue34 = posBlockValue28 + posBlockValue3;
               double posBlockValue35 = posBlockValue29 + posBlockValue4;
               double posBlockValue36 = posBlockValue30 + posBlockValue5;
               float segmentDistanceFade = this.getSegmentDistanceFade(posBlockValue31, posBlockValue32, posBlockValue33, posBlockValue34, posBlockValue35, posBlockValue36);
               float posBlockFactor6 = extrudeFade * segmentDistanceFade;
               float posBlockFactor7 = extrudeFade4 * segmentDistanceFade;
               if (!(posBlockFactor6 < 0.004F) || !(posBlockFactor7 < 0.004F)) {
                  this.drawLineSegment(
                     buffer,
                     posBlockValue31,
                     posBlockValue32,
                     posBlockValue33,
                     posBlockValue34,
                     posBlockValue35,
                     posBlockValue36,
                     MatrixAmountUtils.getValueInt(this.accentColor, 0.42F * posBlockValue * posBlockFactor6),
                     MatrixAmountUtils.getValueInt(this.accentColor, 0.42F * posBlockValue * posBlockFactor7),
                     1.35F * this.resolutionScale
                  );
               }
            }
         }
      }
   }

   public BuildGuideOverlay() {
      this.cellSlots = new BuildGuideOverlay.CellSlot[]{
         new BuildGuideOverlay.CellSlot(), new BuildGuideOverlay.CellSlot(), new BuildGuideOverlay.CellSlot(), new BuildGuideOverlay.CellSlot()
      };
      this.trailSlots = new BuildGuideOverlay.TrailSlot[]{new BuildGuideOverlay.TrailSlot(), new BuildGuideOverlay.TrailSlot(), new BuildGuideOverlay.TrailSlot()};
      this.frame = new BuildGuideOverlay.Frame();
      this.scratchPoints = new double[9];
      this.lineColors = new int[3];
      this.trailPointBuffer = new double[3];
      this.trailTangentBuffer = new double[3];
      this.pathPoints = new double[768];
      this.pathCumulativeDistances = new double[256];
      this.trailGlowColors = new int[256];
      this.trailHaloColors = new int[256];
      this.trailCoreColors = new int[256];
      this.ringPoints = new double[147];
      this.ringColors = new int[49];
      this.targetPosKeys = new long[64];
      this.targetCoords = new int[192];
   }

   private float getSegmentDistanceFade(double posBlockValue, double posBlockValue2, double lowZ, double posBlockValue4, double lowY, double posBlockValue6) {
      double posBlockFactor = posBlockValue4 - posBlockValue;
      double posBlockFactor2 = lowY - posBlockValue2;
      double posBlockFactor3 = posBlockValue6 - lowZ;
      double posBlockDivisor = posBlockFactor * posBlockFactor + posBlockFactor2 * posBlockFactor2 + posBlockFactor3 * posBlockFactor3;
      double posBlockBound = posBlockDivisor < 1.0E-12 ? 0.0 : ((this.cameraX - posBlockValue) * posBlockFactor + (this.cameraY - posBlockValue2) * posBlockFactor2 + (this.cameraZ - lowZ) * posBlockFactor3) / posBlockDivisor;
      double max4 = Math.max(0.0, Math.min(1.0, posBlockBound));
      double sqrt = Math.sqrt(square(posBlockValue + posBlockFactor * max4 - this.cameraX) + square(posBlockValue2 + posBlockFactor2 * max4 - this.cameraY) + square(lowZ + posBlockFactor3 * max4 - this.cameraZ));
      return MatrixAmountUtils.getAmountForFloat((float)((sqrt - 0.2) / 1.0));
   }

   private static long hashCutFrame(BuildGuideOverlay.Frame frame) {
      long posBlockValue = 1469598103934665603L;
      long hashStep2 = hashStep(posBlockValue, frame.startX);
      long hashStep3 = hashStep(hashStep2, frame.endY);
      long hashStep4 = hashStep(hashStep3, frame.startZ);
      long hashStep5 = hashStep(hashStep4, frame.endX);
      long hashStep6 = hashStep(hashStep5, frame.startY);
      long hashStep7 = hashStep(hashStep6, frame.endZ);
      long hashStep8 = hashStep(hashStep7, frame.axisIndex);
      long hashStep9 = hashStep(hashStep8, frame.extrudeDirection);
      long hashStep10 = hashStep(hashStep9, frame.extrudeLength);
      long hashStep11 = hashStep(hashStep10, frame.minBuildY);
      long hashStep12 = hashStep(hashStep11, frame.maxBuildY);
      return hashStep12 == 0L ? 1L : hashStep12;
   }

   private BuildGuideOverlay.TrailSlot getActiveTrailSlot() {
      for (BuildGuideOverlay.TrailSlot trailSlot : this.trailSlots) {
         if (trailSlot.isActiveAndNotFading()) {
            return trailSlot;
         }
      }

      return null;
   }

   private BuildGuideOverlay.CutSlot getActiveCutSlot() {
      for (BuildGuideOverlay.CutSlot classsMin : this.cutSlots) {
         if (classsMin.isActiveAndNotFading()) {
            return classsMin;
         }
      }

      return null;
   }

   private void drawFaceQuad(double posBlockValue, double vertexBuilderValue, double posBlockValue3, int posBlockValue4, int valueInt, double posBlockValue6) {
      if (valueInt >>> 24 != 0) {
         double dirOffset = posBlockValue + 1.0;
         double posBlockValue7 = vertexBuilderValue + 1.0;
         double colOffset = posBlockValue3 + 1.0;
         VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();
         switch (posBlockValue4) {
            case 0:
               this.addQuadXPlane(buffer, posBlockValue - posBlockValue6, vertexBuilderValue, posBlockValue3, posBlockValue7, colOffset, valueInt);
               break;
            case 1:
               this.addQuadXPlane(buffer, dirOffset + posBlockValue6, vertexBuilderValue, posBlockValue3, posBlockValue7, colOffset, valueInt);
               break;
            case 2:
               this.addQuadYPlane(buffer, vertexBuilderValue - posBlockValue6, posBlockValue, posBlockValue3, dirOffset, colOffset, valueInt);
               break;
            case 3:
               this.addQuadYPlane(buffer, posBlockValue7 + posBlockValue6, posBlockValue, posBlockValue3, dirOffset, colOffset, valueInt);
               break;
            case 4:
               this.addQuadZPlane(buffer, posBlockValue3 - posBlockValue6, posBlockValue, vertexBuilderValue, dirOffset, posBlockValue7, valueInt);
               break;
            default:
               this.addQuadZPlane(buffer, colOffset + posBlockValue6, posBlockValue, vertexBuilderValue, dirOffset, posBlockValue7, valueInt);
         }
      }
   }

   private double distanceToBox(double posBlockValue, double lowY, double lowZ, double posBlockValue4, double posBlockValue5, double posBlockValue6) {
      double posBlockFactor = Math.max(0.0, Math.max(posBlockValue - this.cameraX, this.cameraX - posBlockValue4));
      double max2 = Math.max(0.0, Math.max(lowY - this.cameraY, this.cameraY - posBlockValue5));
      double max3 = Math.max(0.0, Math.max(lowZ - this.cameraZ, this.cameraZ - posBlockValue6));
      return Math.sqrt(posBlockFactor * posBlockFactor + max2 * max2 + max3 * max3);
   }

   private void drawBoxFaces(double posBlockValue, double lowY, double lowZ, double posBlockValue4, double posBlockValue5, double posBlockValue6, int valueInt, double posBlockValue8) {
      this.addBoxFaces(VertexBuilderUtils.getBufferIMMEDIATE(), posBlockValue, lowY, lowZ, posBlockValue4, posBlockValue5, posBlockValue6, valueInt, posBlockValue8);
   }

   private int findSegmentIndex(int posBlockValue, double posBlockLimit) {
      int posBlockBound = 0;
      int index2 = posBlockValue - 1;

      while (index2 - posBlockBound > 1) {
         int index = posBlockBound + index2 >>> 1;
         if (this.pathCumulativeDistances[index] <= posBlockLimit) {
            posBlockBound = index;
         } else {
            index2 = index;
         }
      }

      return Math.min(posBlockBound, posBlockValue - 2);
   }

   private void drawEdgeWithCorners(
      VertexConsumer vertices, double posBlockValue, double lowY, double lowZ, double posBlockValue4, double posBlockValue5, double posBlockValue6, int valueInt, int scaleAlpha8, double posBlockBound, boolean posBlockFlag, boolean posBlockFlag2
   ) {
      double posBlockFactor = posBlockValue4 - posBlockValue;
      double posBlockFactor2 = posBlockValue5 - lowY;
      double posBlockFactor3 = posBlockValue6 - lowZ;
      double distance = Math.sqrt(posBlockFactor * posBlockFactor + posBlockFactor2 * posBlockFactor2 + posBlockFactor3 * posBlockFactor3);
      if (!(distance < 1.0E-6)) {
         float segmentDistanceFade = this.getSegmentDistanceFade(posBlockValue, lowY, lowZ, posBlockValue4, posBlockValue5, posBlockValue6);
         if (!(segmentDistanceFade < 0.004F)) {
            valueInt = scaleAlpha(valueInt, segmentDistanceFade);
            scaleAlpha8 = scaleAlpha(scaleAlpha8, segmentDistanceFade);
            double posBlockFactor4 = posBlockFactor / distance;
            double posBlockFactor5 = posBlockFactor2 / distance;
            double posBlockFactor6 = posBlockFactor3 / distance;
            double posBlockFactor7 = posBlockFlag ? Math.min(posBlockBound, distance * 0.42) : 0.0;
            double posBlockFactor8 = posBlockFlag2 ? Math.min(posBlockBound, distance * 0.42) : 0.0;
            float planeValue = 1.35F * this.resolutionScale;
            if (posBlockFactor7 > 0.0) {
               this.drawLineSegment(vertices, posBlockValue, lowY, lowZ, posBlockValue + posBlockFactor4 * posBlockFactor7, lowY + posBlockFactor5 * posBlockFactor7, lowZ + posBlockFactor6 * posBlockFactor7, scaleAlpha8, scaleAlpha8, planeValue);
            }

            if (distance - posBlockFactor7 - posBlockFactor8 > 1.0E-4) {
               this.drawLineSegment(
                  vertices,
                  posBlockValue + posBlockFactor4 * posBlockFactor7,
                  lowY + posBlockFactor5 * posBlockFactor7,
                  lowZ + posBlockFactor6 * posBlockFactor7,
                  posBlockValue4 - posBlockFactor4 * posBlockFactor8,
                  posBlockValue5 - posBlockFactor5 * posBlockFactor8,
                  posBlockValue6 - posBlockFactor6 * posBlockFactor8,
                  valueInt,
                  valueInt,
                  planeValue
               );
            }

            if (posBlockFactor8 > 0.0) {
               this.drawLineSegment(vertices, posBlockValue4 - posBlockFactor4 * posBlockFactor8, posBlockValue5 - posBlockFactor5 * posBlockFactor8, posBlockValue6 - posBlockFactor6 * posBlockFactor8, posBlockValue4, posBlockValue5, posBlockValue6, scaleAlpha8, scaleAlpha8, planeValue);
            }
         }
      }
   }

   private static long hashStep(long posBlockValue, int posBlockValue2) {
      return (posBlockValue ^ posBlockValue2 & 4294967295L) * 1099511628211L;
   }

   public BuildGuideOverlay.Frame getFrame() {
      return this.frame;
   }

   private BuildGuideOverlay.CellSlot getActiveCellSlot() {
      for (BuildGuideOverlay.CellSlot cellSlot : this.cellSlots) {
         if (cellSlot.isActiveAndNotFading()) {
            return cellSlot;
         }
      }

      return null;
   }

   private void renderCellSlot(BuildGuideOverlay.CellSlot cellSlot, float posBlockFactor, float posBlockValue) {
      double x = cellSlot.x;
      double y = cellSlot.y;
      double z = cellSlot.z;
      if (!(this.distanceToBox(x, y, z, x + 1.0, y + 1.0, z + 1.0) > 192.0)) {
         int valueInt = MatrixAmountUtils.getValueInt(this.accentColor, (0.065F + 0.04F * posBlockValue) * posBlockFactor);
         this.addBoxFaces(VertexBuilderUtils.getBuffer(), x, y, z, x + 1.0, y + 1.0, z + 1.0, valueInt, 0.006);
      }
   }

   private static boolean isPlayerAwayFromBlock(BlockPos pos, double posBlockValue, double posBlockValue2, double posBlockValue3) {
      double posBlockFactor = pos.getX() + 0.5 - posBlockValue;
      double posBlockFactor2 = pos.getZ() + 0.5 - posBlockValue3;
      return posBlockFactor * posBlockFactor + posBlockFactor2 * posBlockFactor2 > 0.5625 || Math.abs(pos.getY() - posBlockValue2) >= 0.9;
   }

   private void assignTrailTarget(BuildGuideOverlay.TrailSlot trailSlot, BuildGuideOverlay.Frame frame, boolean posBlockFlag, double posBlockValue, double posBlockValue2, double posBlockValue3) {
      trailSlot.routeSource = frame.pathPositions;
      trailSlot.targetX = frame.targetBlock.getX();
      trailSlot.targetY = frame.targetBlock.getY();
      trailSlot.targetZ = frame.targetBlock.getZ();
      trailSlot.straightLine = !posBlockFlag;
      trailSlot.routeKeyCount = 0;
      if (!posBlockFlag) {
         trailSlot.pointCount = 0;
         trailSlot.endX = trailSlot.targetX + 0.5;
         trailSlot.endY = trailSlot.targetY;
         trailSlot.endZ = trailSlot.targetZ + 0.5;
      } else {
         int value = this.fillPosKeys(frame.pathPositions);
         if (trailSlot.routeKeys.length < value) {
            trailSlot.routeKeys = new long[Math.max(value, 16)];
         }

         System.arraycopy(this.targetPosKeys, 0, trailSlot.routeKeys, 0, value);
         trailSlot.routeKeyCount = value;
         if (this.targetCoords.length < value * 3) {
            this.targetCoords = new int[value * 3];
         }

         for (int i = 0; i < value; i++) {
            BlockPos blockPos = frame.pathPositions.get(i);
            this.targetCoords[i * 3] = blockPos.getX();
            this.targetCoords[i * 3 + 1] = blockPos.getY();
            this.targetCoords[i * 3 + 2] = blockPos.getZ();
         }

         double[] posBlockValues = PolylinePathMath.trimPathToPosition(posBlockValue, posBlockValue2, posBlockValue3, PolylinePathMath.blockPathToPoints(this.targetCoords, value));
         double[] smoothPath = PolylinePathMath.smoothPath(posBlockValues);
         int posBlockSize = PolylinePathMath.estimateSampleCount(smoothPath, 0.2);
         trailSlot.routePoints = new double[posBlockSize * 3];
         trailSlot.cumulativeLengths = new double[posBlockSize];
         trailSlot.pointCount = PolylinePathMath.resampleWithDistances(smoothPath, 0.2, trailSlot.routePoints, trailSlot.cumulativeLengths);
         if (trailSlot.pointCount == 0) {
            trailSlot.straightLine = true;
            trailSlot.endX = trailSlot.targetX + 0.5;
            trailSlot.endY = trailSlot.targetY;
            trailSlot.endZ = trailSlot.targetZ + 0.5;
         } else {
            trailSlot.endX = trailSlot.routePoints[(trailSlot.pointCount - 1) * 3];
            trailSlot.endY = trailSlot.routePoints[(trailSlot.pointCount - 1) * 3 + 1];
            trailSlot.endZ = trailSlot.routePoints[(trailSlot.pointCount - 1) * 3 + 2];
         }
      }
   }

   private void addBoxFaces(VertexConsumer vertices, double matrixValue, double lowY, double lowZ, double matrixValue4, double matrixValue5, double matrixValue6, int valueInt, double matrixValue8) {
      if (valueInt >>> 24 != 0) {
         float amountX = VertexBuilderUtils.getAmountX(matrixValue - matrixValue8);
         float matrixValue10 = VertexBuilderUtils.getAmount(lowY - matrixValue8);
         float amountForDouble11 = VertexBuilderUtils.getAmountForDouble(lowZ - matrixValue8);
         float amountX12 = VertexBuilderUtils.getAmountX(matrixValue4 + matrixValue8);
         float deltaY = VertexBuilderUtils.getAmount(matrixValue5 + matrixValue8);
         float amountForDouble = VertexBuilderUtils.getAmountForDouble(matrixValue6 + matrixValue8);
         VertexBuilderUtils.handleVertexColor(
            vertices, this.positionMatrix, amountX, matrixValue10, amountForDouble11, valueInt, amountX12, matrixValue10, amountForDouble11, valueInt, amountX12, matrixValue10, amountForDouble, valueInt, amountX, matrixValue10, amountForDouble, valueInt
         );
         VertexBuilderUtils.handleVertexColor(
            vertices, this.positionMatrix, amountX, deltaY, amountForDouble11, valueInt, amountX, deltaY, amountForDouble, valueInt, amountX12, deltaY, amountForDouble, valueInt, amountX12, deltaY, amountForDouble11, valueInt
         );
         VertexBuilderUtils.handleVertexColor(
            vertices, this.positionMatrix, amountX, matrixValue10, amountForDouble11, valueInt, amountX, deltaY, amountForDouble11, valueInt, amountX12, deltaY, amountForDouble11, valueInt, amountX12, matrixValue10, amountForDouble11, valueInt
         );
         VertexBuilderUtils.handleVertexColor(
            vertices, this.positionMatrix, amountX, matrixValue10, amountForDouble, valueInt, amountX12, matrixValue10, amountForDouble, valueInt, amountX12, deltaY, amountForDouble, valueInt, amountX, deltaY, amountForDouble, valueInt
         );
         VertexBuilderUtils.handleVertexColor(
            vertices, this.positionMatrix, amountX, matrixValue10, amountForDouble11, valueInt, amountX, matrixValue10, amountForDouble, valueInt, amountX, deltaY, amountForDouble, valueInt, amountX, deltaY, amountForDouble11, valueInt
         );
         VertexBuilderUtils.handleVertexColor(
            vertices, this.positionMatrix, amountX12, matrixValue10, amountForDouble11, valueInt, amountX12, deltaY, amountForDouble11, valueInt, amountX12, deltaY, amountForDouble, valueInt, amountX12, matrixValue10, amountForDouble, valueInt
         );
      }
   }

   private void ensurePathCapacity(int posBlockLimit) {
      if (this.pathCumulativeDistances.length < posBlockLimit) {
         int posBlockSize = Math.max(posBlockLimit, this.pathCumulativeDistances.length * 2);
         this.pathPoints = new double[posBlockSize * 3];
         this.pathCumulativeDistances = new double[posBlockSize];
         this.trailGlowColors = new int[posBlockSize];
         this.trailHaloColors = new int[posBlockSize];
         this.trailCoreColors = new int[posBlockSize];
      }
   }

   private void updateSlots(long nanoTime, double posBlockValue, double posBlockValue2, double posBlockValue3) {
      BuildGuideOverlay.Frame frame3 = this.frame;
      BuildGuideOverlay.AreaSlot activeAreaSlot = this.getActiveAreaSlot();
      boolean posBlockFlag = frame3.areaActive && frame3.guidesEnabled;
      if (activeAreaSlot != null
         && (
            !posBlockFlag
               || activeAreaSlot.lowX != frame3.areaLowX
               || activeAreaSlot.lowY != frame3.areaLowY
               || activeAreaSlot.lowZ != frame3.areaLowZ
               || activeAreaSlot.highX != frame3.areaHighX
               || activeAreaSlot.highY != frame3.areaHighY
               || activeAreaSlot.highZ != frame3.areaHighZ
         )) {
         activeAreaSlot.startFadeOut(nanoTime);
         activeAreaSlot = null;
      }

      if (posBlockFlag && activeAreaSlot == null) {
         BuildGuideOverlay.AreaSlot timeFlag = acquireSlot(this.areaSlots);
         timeFlag.lowX = frame3.areaLowX;
         timeFlag.lowY = frame3.areaLowY;
         timeFlag.lowZ = frame3.areaLowZ;
         timeFlag.highX = frame3.areaHighX;
         timeFlag.highY = frame3.areaHighY;
         timeFlag.highZ = frame3.areaHighZ;
         timeFlag.activate(nanoTime);
      }

      BuildGuideOverlay.CutSlot classsMin = this.getActiveCutSlot();
      boolean posBlockFlag2 = frame3.cutActive && frame3.guidesEnabled;
      long posBlockValue4 = posBlockFlag2 ? hashCutFrame(frame3) : 0L;
      if (classsMin != null && posBlockFlag2 && classsMin.frameHash != posBlockValue4) {
         if (canReuseCutSlot(classsMin, frame3)) {
            float posBlockFactor = classsMin.getAnimationFactor(nanoTime);
            double posBlockValue5 = classsMin.minX + classsMin.offsetX * posBlockFactor;
            double posBlockValue6 = classsMin.minY + classsMin.offsetY * posBlockFactor;
            double posBlockValue7 = classsMin.minZ + classsMin.offsetZ * posBlockFactor;
            this.rebuildCutSlot(classsMin, frame3, posBlockValue4);
            classsMin.offsetX = posBlockValue5 - classsMin.minX;
            classsMin.offsetY = posBlockValue6 - classsMin.minY;
            classsMin.offsetZ = posBlockValue7 - classsMin.minZ;
            classsMin.animStartNanos = nanoTime;
         } else {
            classsMin.startFadeOut(nanoTime);
            classsMin = null;
         }
      }

      if (classsMin != null && !posBlockFlag2 && frame3.guidesEnabled) {
         if (this.cutMissingStartNanos == 0L) {
            this.cutMissingStartNanos = nanoTime;
         }

         if (nanoTime - this.cutMissingStartNanos > 150000000L) {
            classsMin.startFadeOut(nanoTime);
            classsMin = null;
         }
      } else if (classsMin != null && !posBlockFlag2) {
         classsMin.startFadeOut(nanoTime);
         classsMin = null;
      }

      if (posBlockFlag2) {
         this.cutMissingStartNanos = 0L;
      }

      if (posBlockFlag2 && classsMin == null) {
         BuildGuideOverlay.CutSlot cutSlot = acquireSlot(this.cutSlots);
         this.rebuildCutSlot(cutSlot, frame3, posBlockValue4);
         cutSlot.activate(nanoTime);
      }

      BuildGuideOverlay.CellSlot activeCellSlot = this.getActiveCellSlot();
      BlockPos blockPos = frame3.guidesEnabled ? frame3.cellBlock : null;
      if (activeCellSlot != null && blockPos != null && (activeCellSlot.x != blockPos.getX() || activeCellSlot.y != blockPos.getY() || activeCellSlot.z != blockPos.getZ())) {
         activeCellSlot.startFadeOut(nanoTime);
         activeCellSlot = null;
      }

      if (activeCellSlot != null && blockPos == null) {
         if (this.cellMissingStartNanos == 0L) {
            this.cellMissingStartNanos = nanoTime;
         }

         if (nanoTime - this.cellMissingStartNanos > 150000000L || !frame3.guidesEnabled) {
            activeCellSlot.startFadeOut(nanoTime);
            activeCellSlot = null;
         }
      }

      if (blockPos != null) {
         this.cellMissingStartNanos = 0L;
      }

      if (blockPos != null && activeCellSlot == null) {
         BuildGuideOverlay.CellSlot acquireSlot3 = acquireSlot(this.cellSlots);
         acquireSlot3.x = blockPos.getX();
         acquireSlot3.y = blockPos.getY();
         acquireSlot3.z = blockPos.getZ();
         acquireSlot3.activate(nanoTime);
      }

      this.updateTrailSlot(nanoTime, posBlockValue, posBlockValue2, posBlockValue3);
   }

   private static double square(double posBlockFactor) {
      return posBlockFactor * posBlockFactor;
   }

   public void renderGuidesSafely(boolean posBlockFlag, float posBlockValue) {
      if (!this.disabledAfterError) {
         try {
            this.renderGuides(posBlockFlag, posBlockValue);
         } catch (Throwable e) {
            this.disabledAfterError = true;
         }
      }
   }

   private int fillPosKeys(List<BlockPos> list) {
      int times = list == null ? 0 : list.size();
      if (this.targetPosKeys.length < times) {
         this.targetPosKeys = new long[Math.max(times, this.targetPosKeys.length * 2)];
      }

      for (int i = 0; i < times; i++) {
         BlockPos blockPos = (BlockPos)list.get(i);
         this.targetPosKeys[i] = VoxelSurfaceMesher.packPosKey(blockPos.getX(), blockPos.getY(), blockPos.getZ());
      }

      return times;
   }

   private void rebuildCutSlot(BuildGuideOverlay.CutSlot cutSlot, BuildGuideOverlay.Frame frame, long time) {
      cutSlot.frameHash = time;
      cutSlot.offsetX = cutSlot.offsetY = cutSlot.offsetZ = 0.0;
      cutSlot.animStartNanos = 0L;
      cutSlot.minX = Math.min(frame.startX, frame.endX);
      cutSlot.minY = Math.min(frame.endY, frame.startY);
      cutSlot.minZ = Math.min(frame.startZ, frame.endZ);
      cutSlot.maxX = Math.max(frame.startX, frame.endX);
      cutSlot.maxY = Math.max(frame.endY, frame.startY);
      cutSlot.maxZ = Math.max(frame.startZ, frame.endZ);
      int posBlockBound = (cutSlot.maxX - cutSlot.minX + 1) * (cutSlot.maxY - cutSlot.minY + 1) * (cutSlot.maxZ - cutSlot.minZ + 1);
      long[] longs = new long[Math.max(1, posBlockBound)];
      int posBlockCounter = 0;

      for (int i = cutSlot.minX; i <= cutSlot.maxX; i++) {
         for (int j = cutSlot.minY; j <= cutSlot.maxY; j++) {
            for (int k = cutSlot.minZ; k <= cutSlot.maxZ; k++) {
               longs[posBlockCounter++] = VoxelSurfaceMesher.packPosKey(i, j, k);
            }
         }
      }

      cutSlot.outlineEdges = VoxelSurfaceMesher.buildBoundarySegments(longs, posBlockCounter);
      cutSlot.fillFaces = VoxelSurfaceMesher.buildExposedFaces(longs, posBlockCounter);
      cutSlot.growAxis = -1;
      cutSlot.extrudeEdges = new int[0];
      if (frame.axisIndex >= 0 && frame.extrudeLength > 0 && frame.extrudeDirection != 0) {
         int posBlockValue = frame.axisIndex == 0 ? cutSlot.minX : (frame.axisIndex == 1 ? cutSlot.minY : cutSlot.minZ);
         int posBlockValue2 = frame.axisIndex == 0 ? cutSlot.maxX : (frame.axisIndex == 1 ? cutSlot.maxY : cutSlot.maxZ);
         int value = frame.extrudeDirection > 0 ? 1 : -1;
         long[] blockPosValues = new long[posBlockCounter * (frame.extrudeLength + 1)];
         System.arraycopy(longs, 0, blockPosValues, 0, posBlockCounter);
         int blockPosValue = posBlockCounter;
         int value2 = 0;

         for (int m = 1; m <= frame.extrudeLength; m++) {
            int posBlockLimit = value > 0 ? posBlockValue2 + m : posBlockValue - m;
            if (posBlockLimit < frame.minBuildY || posBlockLimit > frame.maxBuildY) {
               break;
            }

            value2 = m;

            for (int p = 0; p < posBlockCounter; p++) {
               long posBlockValue3 = longs[p];
               int unpackX = VoxelSurfaceMesher.unpackX(posBlockValue3);
               int unpackY = VoxelSurfaceMesher.unpackY(posBlockValue3);
               int unpackZ = VoxelSurfaceMesher.unpackZ(posBlockValue3);
               switch (frame.axisIndex) {
                  case 0:
                     unpackX = posBlockLimit;
                     break;
                  case 1:
                     unpackY = posBlockLimit;
                     break;
                  default:
                     unpackZ = posBlockLimit;
               }

               blockPosValues[blockPosValue++] = VoxelSurfaceMesher.packPosKey(unpackX, unpackY, unpackZ);
            }
         }

         if (value2 != 0) {
            cutSlot.extrudeEdges = VoxelSurfaceMesher.getValues(blockPosValues, blockPosValue, longs, posBlockCounter);
            cutSlot.growAxis = frame.axisIndex;
            cutSlot.faceDirection = value;
            cutSlot.faceCoordinate = value > 0 ? posBlockValue2 + 1 : posBlockValue;
            cutSlot.extrudeSteps = value2;
         }
      }
   }

   private static float getExtrudeFade(BuildGuideOverlay.CutSlot cutSlot, double posBlockValue, double posBlockValue2, double posBlockValue3) {
      double posBlockValue4 = cutSlot.growAxis == 0 ? posBlockValue : (cutSlot.growAxis == 1 ? posBlockValue2 : posBlockValue3);
      double posBlockValue5 = (posBlockValue4 - cutSlot.faceCoordinate) * cutSlot.faceDirection;
      float max6 = (float)Math.max(0.0, Math.min(1.0, posBlockValue5 / Math.max(1, cutSlot.extrudeSteps)));
      float posBlockFactor = 1.0F - max6;
      return posBlockFactor * posBlockFactor;
   }

   private void renderTrailChevrons(int cameraValue, double cameraDivisor, float cameraValue2, long cameraValue3) {
      double cameraValue4 = cameraValue3 / 1.0E9 * 1.5 % 1.25;
      VertexConsumer buffer = VertexBuilderUtils.getBuffer();
      VertexConsumer bufferIMMEDIATE = VertexBuilderUtils.getBufferIMMEDIATE();
      float planeValue = 2.0F * this.resolutionScale;

      for (int i = 0; i < 256; i++) {
         double cameraLimit = i * 1.25 + (1.25 - cameraValue4);
         if (cameraLimit > cameraDivisor - 0.9) {
            break;
         }

         if (!(cameraLimit < 0.55)) {
            double posBlockLimit = cameraDivisor - cameraLimit;
            float cameraFactor = MatrixAmountUtils.getAmountForFloat((float)((posBlockLimit - 0.9) / 0.6))
               * MatrixAmountUtils.getAmountForFloat((float)((cameraLimit - 0.55) / 0.5))
               * (1.0F - 0.5F * MatrixAmountUtils.getAmountForFloat((float)(posBlockLimit / cameraDivisor)));
            if (!(cameraFactor < 0.01F)) {
               int index = this.findSegmentIndex(cameraValue, posBlockLimit);
               double cameraFactor2 = this.pathCumulativeDistances[index + 1] - this.pathCumulativeDistances[index] > 1.0E-9
                  ? (posBlockLimit - this.pathCumulativeDistances[index]) / (this.pathCumulativeDistances[index + 1] - this.pathCumulativeDistances[index])
                  : 0.0;
               double cameraValue7 = this.pathPoints[index * 3] + (this.pathPoints[index * 3 + 3] - this.pathPoints[index * 3]) * cameraFactor2;
               double cameraValue8 = this.pathPoints[index * 3 + 1] + (this.pathPoints[index * 3 + 4] - this.pathPoints[index * 3 + 1]) * cameraFactor2;
               double cameraValue9 = this.pathPoints[index * 3 + 2] + (this.pathPoints[index * 3 + 5] - this.pathPoints[index * 3 + 2]) * cameraFactor2;
               int max10 = Math.max(0, index - 1);
               int min11 = Math.min(cameraValue - 1, index + 2);
               double cameraFactor3 = this.pathPoints[min11 * 3] - this.pathPoints[max10 * 3];
               double cameraFactor4 = this.pathPoints[min11 * 3 + 2] - this.pathPoints[max10 * 3 + 2];
               double distance = Math.sqrt(cameraFactor3 * cameraFactor3 + cameraFactor4 * cameraFactor4);
               if (!(distance < 1.0E-4)) {
                  double cameraValue12 = cameraFactor3 / distance;
                  double cameraValue13 = cameraFactor4 / distance;
                  double cameraValue14 = -cameraValue13;
                  double cameraValue15 = 0.012;
                  this.scratchPoints[0] = cameraValue7 - cameraValue12 * 0.1 + cameraValue14 * 0.17;
                  this.scratchPoints[1] = cameraValue8 + cameraValue15;
                  this.scratchPoints[2] = cameraValue9 - cameraValue13 * 0.1 + cameraValue12 * 0.17;
                  this.scratchPoints[3] = cameraValue7 + cameraValue12 * 0.1;
                  this.scratchPoints[4] = cameraValue8 + cameraValue15;
                  this.scratchPoints[5] = cameraValue9 + cameraValue13 * 0.1;
                  this.scratchPoints[6] = cameraValue7 - cameraValue12 * 0.1 - cameraValue14 * 0.17;
                  this.scratchPoints[7] = cameraValue8 + cameraValue15;
                  this.scratchPoints[8] = cameraValue9 - cameraValue13 * 0.1 - cameraValue12 * 0.17;
                  int valueInt = MatrixAmountUtils.getValueInt(this.lightAccentColor, 0.95F * cameraValue2 * cameraFactor);
                  this.lineColors[0] = this.lineColors[1] = this.lineColors[2] = MatrixAmountUtils.getValueInt(this.lightAccentColor, 0.32F * cameraValue2 * cameraFactor);
                  VectorPlaneUtils.handleAmountsPlane(bufferIMMEDIATE, this.positionMatrix, this.camera, this.scratchPoints, this.lineColors, 3, planeValue, 1.0F, false, false);
                  this.lineColors[0] = this.lineColors[1] = this.lineColors[2] = valueInt;
                  VectorPlaneUtils.handleAmountsPlane(buffer, this.positionMatrix, this.camera, this.scratchPoints, this.lineColors, 3, planeValue, 1.0F, false, false);
               }
            }
         }
      }
   }

   private static final class AreaSlot extends BuildGuideOverlay.Slot {
      int highY;
      int lowZ;
      int lowY;
      int highZ;
      int lowX;
      int highX;
   }

   private static final class CellSlot extends BuildGuideOverlay.Slot {
      int x;
      int y;
      int z;
   }

   private static final class CutSlot extends BuildGuideOverlay.Slot {
      int maxZ;
      int minZ;
      long animStartNanos;
      double offsetZ;
      int extrudeSteps;
      int minX;
      int maxX;
      long frameHash;
      int[] extrudeEdges;
      int[] fillFaces;
      double offsetX;
      int growAxis;
      int[] outlineEdges = new int[0];
      int minY;
      double offsetY;
      int faceCoordinate;
      int faceDirection;
      int maxY;

      private CutSlot() {
         this.extrudeEdges = new int[0];
         this.fillFaces = new int[0];
         this.growAxis = -1;
      }

      float getAnimationFactor(long cutSlotValue) {
         if (this.animStartNanos == 0L) {
            return 0.0F;
         } else {
            float cutSlotLimit = (float)(cutSlotValue - this.animStartNanos) / 2.3E8F;
            if (cutSlotLimit >= 1.0F) {
               this.animStartNanos = 0L;
               return 0.0F;
            } else {
               float cutSlotFactor = 1.0F - Math.max(0.0F, cutSlotLimit);
               return cutSlotFactor * cutSlotFactor * cutSlotFactor;
            }
         }
      }
   }

   public static final class Frame {
      public boolean areaActive;
      public List<BlockPos> pathPositions;
      public int areaLowX;
      public boolean cutActive;
      public BlockPos targetBlock;
      public boolean targetActive;
      public int maxBuildY;
      public int areaHighY;
      public int extrudeDirection;
      public BlockPos cellBlock;
      public int endZ;
      public boolean guidesEnabled;
      public int areaHighX;
      public int endY;
      public int startY;
      public int endX;
      public int startZ;
      public int areaLowY;
      public int startX;
      public int areaLowZ;
      public int extrudeLength;
      public boolean trailEnabled;
      public int axisIndex = -1;
      public int areaHighZ;
      public int minBuildY;

      public void setArea(int value, int value2, int value3, int value4, int value5, int value6) {
         this.areaActive = true;
         this.areaLowX = value;
         this.areaLowY = value2;
         this.areaLowZ = value3;
         this.areaHighX = value4;
         this.areaHighY = value5;
         this.areaHighZ = value6;
      }

      public void clear() {
         this.guidesEnabled = this.trailEnabled = this.areaActive = this.cutActive = this.targetActive = false;
         this.axisIndex = -1;
         this.extrudeDirection = this.extrudeLength = 0;
         this.cellBlock = null;
         this.pathPositions = null;
         this.targetBlock = null;
      }

      public void setCutBox(int frameValue, int max, int face, int frameValue3, int min, int miningCornerLimit) {
         this.cutActive = true;
         this.startX = frameValue;
         this.endY = max;
         this.startZ = face;
         this.endX = frameValue3;
         this.startY = min;
         this.endZ = miningCornerLimit;
      }

      public void setExtrude(int value, int value2, int value3, int value4, int value5) {
         this.axisIndex = value;
         this.extrudeDirection = value2;
         this.extrudeLength = value3;
         this.minBuildY = value4;
         this.maxBuildY = value5;
      }
   }

   private static class Slot {
      boolean slotActive;
      long fadeStartNanos;
      long spawnNanos;

      boolean isActiveAndNotFading() {
         return this.slotActive && this.fadeStartNanos == 0L;
      }

      void startFadeOut(long time) {
         if (this.slotActive && this.fadeStartNanos == 0L) {
            this.fadeStartNanos = time;
         }
      }

      float getFadeAlpha(long slotValue, long slotValue2, long slotValue3) {
         if (!this.slotActive) {
            return 0.0F;
         } else {
            float timeFlag = MatrixAmountUtils.getAmountForFloat((float)(slotValue - this.spawnNanos) / (float)slotValue2);
            if (this.fadeStartNanos != 0L) {
               if (slotValue - this.fadeStartNanos >= slotValue3) {
                  this.slotActive = false;
                  return 0.0F;
               }

               timeFlag *= 1.0F - MatrixAmountUtils.getAmountForFloat((float)(slotValue - this.fadeStartNanos) / (float)slotValue3);
            }

            return timeFlag;
         }
      }

      void activate(long time) {
         this.slotActive = true;
         this.spawnNanos = time;
         this.fadeStartNanos = 0L;
      }
   }

   private static final class TrailSlot extends BuildGuideOverlay.Slot {
      int pointCount;
      double endZ;
      boolean straightLine;
      int targetY;
      int targetX;
      int routeKeyCount;
      double endX;
      long[] routeKeys = new long[0];
      int targetZ;
      Object routeSource;
      double[] cumulativeLengths;
      double endY;
      double[] routePoints = new double[0];

      private TrailSlot() {
         this.cumulativeLengths = new double[0];
      }
   }
}
