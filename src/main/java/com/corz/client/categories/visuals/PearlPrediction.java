package com.corz.client.categories.visuals;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.entity.FloorMathUtilsDirection;
import com.corz.client.entity.FloorMathUtilsDirection.Result;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.TickProgress;
import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorPlaneUtils;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import com.corz.client.categories.misc.NameProtect;
import com.corz.client.categories.movement.Freecam;
import ai.auth.Module;
import com.corz.client.utils.GhostPlayerGeometry;
import com.corz.client.utils.PearlGhost;
import com.corz.client.utils.PearlMath;
import com.corz.client.utils.PearlLabel;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.utils.IconsPngType;

public class PearlPrediction extends Module {
   private double lastPosY;
   private final float[] planeTable;
   private final int[] pearlPredictionTable;
   private double ageAmount;
   private final BooleanSetting booleanBase;
   private final int[] table;
   private final BooleanSetting enemyOnly = new BooleanSetting("Enemy Only", "Only draw pearls thrown by other players", false);
   private final double[] boxBounds;
   private double pearlPredictionAmount;
   private final BooleanSetting throwPreview = new BooleanSetting("Throw Preview", "While holding an ender pearl, show where it would land if you threw it now", true);
   private final List<PearlPrediction.Track> thicknessTrackList;
   private double lastPosZ;
   private final DoubleSetting doubleHelper;
   private final List<PearlPrediction.Track> pearlPredictionTrackList;
   private final Int2ObjectOpenHashMap<PearlPrediction.Track> int2ObjectOpenHashMap;
   private final int[] distanceTable;
   private double targetPosZ;
   private final BooleanSetting pearlPredictionBooleanBase;
   private final MatrixStack matrixStack;
   private final int[] segmentColors;
   private int age;
   private final double[] pathPoints;
   private long lastNanoTime;
   private double targetPosY;
   private final BooleanSetting landingBox;
   private final int[] distanceColors;
   private final BooleanSetting throughWalls;
   private WeakReference<ClientWorld> weakReference;
   private final PearlPrediction.Track track;
   private final List<PearlPrediction.Track> flightThicknessItems;

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      this.pearlPredictionTrackList.clear();
      if (mc.world != null && mc.player != null) {
         Camera camera = mc.gameRenderer.getCamera();
         if (camera != null) {
            if (this.weakReference.get() != mc.world) {
               this.updateTrackThickness();
               this.weakReference = new WeakReference<>(mc.world);
            }

            long nanoTime = System.nanoTime();
            float pearlPredictionValue = this.lastNanoTime == 0L ? 0.016F : Math.min(0.1F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
            this.lastNanoTime = nanoTime;
            float tickProgress = queueCommand.tickProgress;
            Vec3d cameraPos = camera.getCameraPos();
            var iterator = this.int2ObjectOpenHashMap.values().iterator();

            while (iterator.hasNext()) {
               PearlPrediction.Track track = (PearlPrediction.Track)iterator.next();
               track.trackAlive = false;
            }

            this.track.trackAlive = false;

            for (Entity entity : mc.world.getEntities()) {
               if (entity instanceof EnderPearlEntity enderPearlEntity && !enderPearlEntity.isRemoved()) {
                  Entity owner = enderPearlEntity.getOwner();
                  if (!this.enemyOnly.isEnabled() || owner != mc.player) {
                     PearlPrediction.Track int2objectopenhashmap = (PearlPrediction.Track)this.int2ObjectOpenHashMap.get(enderPearlEntity.getId());
                     if (int2objectopenhashmap == null) {
                        int2objectopenhashmap = new PearlPrediction.Track();
                        this.int2ObjectOpenHashMap.put(enderPearlEntity.getId(), int2objectopenhashmap);
                     }

                     this.handleDirectionVec(int2objectopenhashmap, enderPearlEntity, owner, tickProgress);
                  }
               }
            }

            if (this.throwPreview.isEnabled() && this.isHandEnder()) {
               this.handleAgeAmount(this.track, tickProgress);
            }

            this.flightThicknessItems.clear();
            var iterator2 = this.int2ObjectOpenHashMap.values().iterator();

            while (iterator2.hasNext()) {
               PearlPrediction.Track pearlPredictionTrack = (PearlPrediction.Track)iterator2.next();
               if (this.canAmountDistance(pearlPredictionTrack, pearlPredictionValue, cameraPos)) {
                  this.flightThicknessItems.add(pearlPredictionTrack);
               }
            }

            this.int2ObjectOpenHashMap.values().removeIf(track3 -> !track3.trackAlive && track3.distanceAmount <= 0.004F);
            if (this.canAmountDistance(this.track, pearlPredictionValue, cameraPos)) {
               this.flightThicknessItems.add(this.track);
            }

            if (!this.flightThicknessItems.isEmpty()) {
               this.flightThicknessItems.sort((track4, track5) -> Double.compare(track5.distanceTo, track4.distanceTo));
               Matrix4f positionMatrix = queueCommand.matrices.peek().getPositionMatrix();
               boolean booleanValue = this.throughWalls.isEnabled();
               float max2 = Math.max(1.2F, (float)this.doubleHelper.getValue() * mc.getWindow().getFramebufferHeight() / 1080.0F);

               for (PearlPrediction.Track track6 : this.flightThicknessItems) {
                  this.handleAmountFlag(queueCommand.matrices, positionMatrix, camera, cameraPos, track6, max2, booleanValue);
               }

               for (PearlPrediction.Track track7 : this.flightThicknessItems) {
                  this.handleAmountsPlane(positionMatrix, camera, track7, max2, booleanValue);
               }

               VertexBuilderUtils.updateDrawImmediate();
               boolean enabled = this.pearlPredictionBooleanBase.isEnabled();

               for (PearlPrediction.Track track8 : this.flightThicknessItems) {
                  boolean pearlPredictionFlag = enabled && track8.distanceActive && (booleanValue || this.canAbsentAmount(cameraPos, track8));
                  track8.thicknessAmount = GhostPlayerGeometry.getAmount(track8.thicknessAmount + (pearlPredictionFlag ? pearlPredictionValue : -pearlPredictionValue) / 0.12F);
                  if (track8.thicknessActive && track8.thicknessAmount > 0.02F) {
                     this.pearlPredictionTrackList.add(track8);
                  }
               }
            }
         }
      }
   }

   private boolean isHandEnder() {
      return mc.player.getMainHandStack().isOf(Items.ENDER_PEARL) || mc.player.getOffHandStack().isOf(Items.ENDER_PEARL);
   }

   public PearlPrediction() {
      super("Pearl Prediction", IconsPngType.VISUAL);
      this.booleanBase = new BooleanSetting("Skin Render", "Show the thrower's skin standing where the pearl will land", true);
      this.landingBox = new BooleanSetting("Landing Box", "Mark the landing spot with a box when the skin is not shown", true);
      this.pearlPredictionBooleanBase = new BooleanSetting("Label", "Show the thrower's name and the time left under the landing spot", true);
      this.throughWalls = new BooleanSetting("Through Walls", "Keep the path and the landing visible behind blocks", true);
      this.doubleHelper = new DoubleSetting("Line Width", "Thickness of the flight path", 2.0, 1.0, 4.0, 0.5);
      this.int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
      this.track = new PearlPrediction.Track();
      this.flightThicknessItems = new ArrayList<>();
      this.pearlPredictionTrackList = new ArrayList<>();
      this.thicknessTrackList = new ArrayList<>();
      this.pathPoints = new double[4584];
      this.planeTable = new float[1528];
      this.pearlPredictionTable = new int[this.planeTable.length];
      this.table = new int[this.planeTable.length];
      this.segmentColors = new int[this.planeTable.length];
      this.boxBounds = new double[6];
      this.distanceTable = new int[2];
      this.distanceColors = new int[2];
      this.matrixStack = new MatrixStack();
      this.weakReference = new WeakReference<>(null);
      this.age = Integer.MIN_VALUE;
      this.addSettings(
         new ConfigSetting[]{this.enemyOnly, this.throwPreview, this.booleanBase, this.landingBox, this.pearlPredictionBooleanBase, this.throughWalls, this.doubleHelper}
      );
   }

   private boolean canDirectionVec(PearlPrediction.Track track, EnderPearlEntity enderPearlEntity, Vec3d vec) {
      if (track.vecDirection != null && track.vecDirection.directionVecActive) {
         int directionVecValue = enderPearlEntity.age - track.directionVecValue - track.vecDirection.flightTicks;
         if (directionVecValue < 0) {
            return false;
         } else if (track.vecDirection.entityValue < 0) {
            return vec.squaredDistanceTo(track.vecDirection.endPos) < 1.0;
         } else {
            Entity entityById = mc.world.getEntityById(track.vecDirection.entityValue);
            return entityById != null && entityById.getBoundingBox().expand(0.35).contains(vec) || track.directionVecActive && directionVecValue < 10;
         }
      } else {
         return false;
      }
   }

   private void handleAmountFlag(MatrixStack matrices, Matrix4f matrix, Camera camera, Vec3d vec, PearlPrediction.Track track, float pearlPredictionValue, boolean pearlPredictionFlag) {
      if (track.trackVisible && track.thicknessActive) {
         float amount = track.ageActive ? track.fadeAlpha * (1.0F - this.getMax(track)) : track.fadeAlpha;
         if (!(amount < 0.04F)) {
            track.visibility = amount;
            track.distanceActive = true;
            boolean pearlPredictionFlag2 = this.booleanBase.isEnabled() && track.texturePath != null;
            if (pearlPredictionFlag2 || this.landingBox.isEnabled()) {
               double vertexColorValue = track.clampCount - vec.x;
               double y = track.distanceCount - vec.y;
               double z = track.smoothedZ - vec.z;
               if (track.vecDirectionActive) {
                  PearlGhost.handleVertexColor(matrix, vertexColorValue, y, z, track.distanceValue, amount);
               }

               if (pearlPredictionFlag2) {
                  PearlGhost.handleDrawBuffer(
                     matrices, vertexColorValue, y, z, track.yaw, track.texturePath, track.partVisibleActive, track.partVisibleValue, track.smoothedAge, amount, track.distanceValue, pearlPredictionFlag
                  );
               } else {
                  this.handleAmountDistance(matrix, camera, track, vertexColorValue, y, z, pearlPredictionValue, pearlPredictionFlag);
               }
            }
         }
      }
   }

   private boolean canAmountClamp(PearlPrediction.Track track, float clampDivisor) {
      if (track.visibility * track.thicknessAmount < 0.02F) {
         return false;
      } else {
         Vec3d windowFramebuffer = VertexConsumerUtilsVector.getWindowFramebuffer(new Vec3d(track.clampCount, track.distanceCount - 0.1, track.smoothedZ));
         if (windowFramebuffer != null && !(windowFramebuffer.z <= 0.0) && !(windowFramebuffer.z >= 1.0)) {
            int value = PearlMath.getMax(track.directionVecAmount);
            if (value != track.clampValue) {
               track.clampValue = value;
               track.clampText = PearlMath.formatText(value);
            }

            track.clamp = MathHelper.clamp(1.12F - (float)track.distanceTo / 56.0F, 0.72F, 1.0F);
            track.clampAmount = (float)(windowFramebuffer.x / clampDivisor);
            track.labelY = (float)(windowFramebuffer.y / clampDivisor) + 5.0F * track.clamp;
            track.rendererFont = PearlLabel.getRendererFont(track.clamp, track.pearlText, track.clampText);
            track.labelHeight = 16.0F * track.clamp;
            return true;
         } else {
            return false;
         }
      }
   }

   private void handleAmountsPlane(Matrix4f matrix, Camera camera, PearlPrediction.Track track, float planeValue, boolean planeFlag) {
      if (track.pathPointCount >= 2 && !(track.vecAmount - track.pathStartDistance < 0.001) && !(track.fadeAlpha < 0.02F)) {
         int planeLimit = VectorPlaneUtils.getValue(track.vecTable, track.pathPointCount, track.pathStartDistance, track.vecAmount, 5, this.pathPoints);
         if (planeLimit >= 2) {
            this.planeTable[0] = 0.0F;

            for (int i = 1; i < planeLimit; i++) {
               double planeFactor = this.pathPoints[i * 3] - this.pathPoints[i * 3 - 3];
               double planeFactor2 = this.pathPoints[i * 3 + 1] - this.pathPoints[i * 3 - 2];
               double planeFactor3 = this.pathPoints[i * 3 + 2] - this.pathPoints[i * 3 - 1];
               this.planeTable[i] = this.planeTable[i - 1] + (float)Math.sqrt(planeFactor * planeFactor + planeFactor2 * planeFactor2 + planeFactor3 * planeFactor3);
            }

            float planeValue2 = this.planeTable[planeLimit - 1];
            boolean directionVecActive2 = !track.vecDirection.directionVecActive;
            int valueForIntFloat = PearlGhost.getValueForIntFloat(track.distanceValue, 0.25F);
            Vector3fc verticalPlane = camera.getVerticalPlane();
            Vector3fc diagonalPlane = camera.getDiagonalPlane();

            for (int j = 0; j < planeLimit; j++) {
               float pearlMathValue = this.planeTable[j];
               float planeFactor4 = track.firstPersonView ? PearlMath.getAmountFloat((pearlMathValue - 0.5F) / 2.5F) : PearlMath.getAmountFloat(pearlMathValue / 0.6F);
               if (directionVecActive2) {
                  planeFactor4 *= PearlMath.getAmountFloat((planeValue2 - pearlMathValue) / 4.0F);
               }

               float planeValue5 = planeFactor4 * track.fadeAlpha;
               if (track.firstPersonView) {
                  float planeFactor5 = PearlMath.getAmount(pearlMathValue, planeValue2);
                  float planeFactor6 = 0.32F * track.planeAmount * planeFactor5;
                  float planeFactor7 = -0.14F * planeFactor5;
                  this.pathPoints[j * 3] = this.pathPoints[j * 3] + (diagonalPlane.x() * planeFactor6 + verticalPlane.x() * planeFactor7);
                  this.pathPoints[j * 3 + 1] = this.pathPoints[j * 3 + 1] + (diagonalPlane.y() * planeFactor6 + verticalPlane.y() * planeFactor7);
                  this.pathPoints[j * 3 + 2] = this.pathPoints[j * 3 + 2] + (diagonalPlane.z() * planeFactor6 + verticalPlane.z() * planeFactor7);
               }

               this.pearlPredictionTable[j] = PearlGhost.getValue(valueForIntFloat, 0.95F * planeValue5);
               this.table[j] = PearlGhost.getValue(track.distanceValue, 0.14F * planeValue5);
               this.segmentColors[j] = PearlGhost.getValue(track.distanceValue, 0.3F * planeValue5);
            }

            boolean planeFlag3 = !directionVecActive2;
            if (planeFlag) {
               VectorPlaneUtils.handleAmountsPlane(VertexBuilderUtils.getBufferIMMEDIATE(), matrix, camera, this.pathPoints, this.segmentColors, planeLimit, planeValue, 1.25F, false, planeFlag3);
            }

            VertexConsumer buffer = VertexBuilderUtils.getBuffer();
            float planeValue6 = planeValue * 2.0F + 2.0F;
            VectorPlaneUtils.handleAmountsPlane(buffer, matrix, camera, this.pathPoints, this.table, planeLimit, planeValue6, planeValue6, false, false);
            VectorPlaneUtils.handleAmountsPlane(buffer, matrix, camera, this.pathPoints, this.pearlPredictionTable, planeLimit, planeValue, 1.25F, false, planeFlag3);
         }
      }
   }

   private static void handle(
      VertexConsumer vertices, Matrix4f matrix, float pearlPredictionValue, float pearlPredictionValue2, float amountX, float amountForDouble, float pearlPredictionValue5, float deltaY, float widthOffset, int pearlPredictionValue8, int pearlPredictionValue9, int pearlPredictionValue10, int valueInt
   ) {
      if (deltaY - pearlPredictionValue5 > 0.001F) {
         VertexBuilderUtils.handleVertexColor(vertices, matrix, pearlPredictionValue, pearlPredictionValue5, pearlPredictionValue2, pearlPredictionValue8, amountX, pearlPredictionValue5, amountForDouble, pearlPredictionValue8, amountX, deltaY, amountForDouble, pearlPredictionValue9, pearlPredictionValue, deltaY, pearlPredictionValue2, pearlPredictionValue9);
      }

      if (widthOffset - deltaY > 0.001F) {
         VertexBuilderUtils.handleVertexColor(vertices, matrix, pearlPredictionValue, deltaY, pearlPredictionValue2, pearlPredictionValue10, amountX, deltaY, amountForDouble, pearlPredictionValue10, amountX, widthOffset, amountForDouble, valueInt, pearlPredictionValue, widthOffset, pearlPredictionValue2, valueInt);
      }
   }

   private boolean canAmountDistance(PearlPrediction.Track track, float distanceValue, Vec3d vec) {
      track.distanceActive = false;
      track.distanceAmount = GhostPlayerGeometry.getAmount(track.distanceAmount + (track.trackAlive ? distanceValue / 0.14F : -distanceValue / 0.24F));
      if (track.vecDirection != null && !(track.distanceAmount <= 0.004F)) {
         track.fadeAlpha = 1.0F - (1.0F - track.distanceAmount) * (1.0F - track.distanceAmount) * (1.0F - track.distanceAmount);
         if (track.trackVisible) {
            double distanceFactor = track.x - track.clampCount;
            double distanceFactor2 = track.y - track.distanceCount;
            double distanceFactor3 = track.z - track.smoothedZ;
            if (track.thicknessActive && !(distanceFactor * distanceFactor + distanceFactor2 * distanceFactor2 + distanceFactor3 * distanceFactor3 > 2.25)) {
               double distanceFactor4 = 1.0 - Math.exp(-distanceValue / 0.05);
               track.clampCount += distanceFactor * distanceFactor4;
               track.distanceCount += distanceFactor2 * distanceFactor4;
               track.smoothedZ += distanceFactor3 * distanceFactor4;
            } else {
               track.clampCount = track.x;
               track.distanceCount = track.y;
               track.smoothedZ = track.z;
               track.smoothedAge = track.ageAmount;
               track.thicknessActive = true;
            }

            track.distanceTo = Math.sqrt(vec.squaredDistanceTo(track.clampCount, track.distanceCount, track.smoothedZ));
         } else {
            track.thicknessActive = false;
            track.distanceTo = track.entityPos == null ? 0.0 : Math.sqrt(vec.squaredDistanceTo(track.entityPos));
         }

         track.smoothedAge = track.smoothedAge + (track.ageAmount - track.smoothedAge) * (1.0F - (float)Math.exp(-distanceValue / 0.08F));
         return true;
      } else {
         if (!track.trackAlive) {
            track.thicknessActive = false;
            track.thicknessAmount = 0.0F;
         }

         return false;
      }
   }

   private boolean canAbsentAmount(Vec3d vec, PearlPrediction.Track track) {
      return !track.thicknessActive
         ? false
         : mc.world
               .raycast(
                  new RaycastContext(vec, new Vec3d(track.clampCount, track.distanceCount + 0.9F, track.smoothedZ), ShapeType.VISUAL, FluidHandling.NONE, ShapeContext.absent())
               )
               .getType()
            == Type.MISS;
   }

   private void handleAmounts(Matrix4f matrix, Camera camera, double pearlPredictionValue, double pearlPredictionValue2, double pearlPredictionValue3, double pearlPredictionValue4, double pearlPredictionValue5, double pearlPredictionValue6, float planeValue, boolean pearlPredictionFlag) {
      this.boxBounds[0] = pearlPredictionValue;
      this.boxBounds[1] = pearlPredictionValue2;
      this.boxBounds[2] = pearlPredictionValue3;
      this.boxBounds[3] = pearlPredictionValue4;
      this.boxBounds[4] = pearlPredictionValue5;
      this.boxBounds[5] = pearlPredictionValue6;
      if (pearlPredictionFlag) {
         VectorPlaneUtils.handleAmountsPlane(VertexBuilderUtils.getBufferIMMEDIATE(), matrix, camera, this.boxBounds, this.distanceColors, 2, planeValue, 1.25F, false, false);
      }

      VectorPlaneUtils.handleAmountsPlane(VertexBuilderUtils.getBuffer(), matrix, camera, this.boxBounds, this.distanceTable, 2, planeValue, 1.25F, false, false);
   }

   private void handle(Matrix4f matrix, Camera camera, double pearlPredictionValue, double pearlPredictionValue2, double pearlPredictionValue3, double pearlPredictionValue4, double pearlPredictionValue5, float planeValue, boolean pearlPredictionFlag) {
      this.handleAmounts(matrix, camera, pearlPredictionValue, pearlPredictionValue5, pearlPredictionValue2, pearlPredictionValue3, pearlPredictionValue5, pearlPredictionValue2, planeValue, pearlPredictionFlag);
      this.handleAmounts(matrix, camera, pearlPredictionValue3, pearlPredictionValue5, pearlPredictionValue2, pearlPredictionValue3, pearlPredictionValue5, pearlPredictionValue4, planeValue, pearlPredictionFlag);
      this.handleAmounts(matrix, camera, pearlPredictionValue3, pearlPredictionValue5, pearlPredictionValue4, pearlPredictionValue, pearlPredictionValue5, pearlPredictionValue4, planeValue, pearlPredictionFlag);
      this.handleAmounts(matrix, camera, pearlPredictionValue, pearlPredictionValue5, pearlPredictionValue4, pearlPredictionValue, pearlPredictionValue5, pearlPredictionValue2, planeValue, pearlPredictionFlag);
   }

   private void handleAmountDistance(Matrix4f matrix, Camera camera, PearlPrediction.Track track, double distanceValue, double y, double z, float distanceValue4, boolean distanceFlag) {
      float distanceValue5 = (float)(distanceValue - 0.3F);
      float amountX = (float)(distanceValue + 0.3F);
      float amountForDouble = (float)(z - 0.3F);
      float distanceValue8 = (float)(z + 0.3F);
      float width2 = (float)y;
      float widthOffset = (float)(y + 1.8F);
      float deltaY = width2 + (widthOffset - width2) * GhostPlayerGeometry.getAmount(track.smoothedAge);
      float visibility = track.visibility;
      int distanceValue10 = PearlGhost.getValue(track.distanceValue, 0.22F * visibility);
      int distanceValue11 = PearlGhost.getValue(track.distanceValue, 0.12F * visibility);
      int valueInt = PearlGhost.getValue(track.distanceValue, 0.06F * visibility);
      int distanceValue13 = PearlGhost.getValue(track.distanceValue, 0.02F * visibility);
      VertexConsumer buffer = VertexBuilderUtils.getBuffer();
      handle(buffer, matrix, distanceValue5, amountForDouble, amountX, amountForDouble, width2, deltaY, widthOffset, distanceValue10, distanceValue11, valueInt, distanceValue13);
      handle(buffer, matrix, amountX, amountForDouble, amountX, distanceValue8, width2, deltaY, widthOffset, distanceValue10, distanceValue11, valueInt, distanceValue13);
      handle(buffer, matrix, amountX, distanceValue8, distanceValue5, distanceValue8, width2, deltaY, widthOffset, distanceValue10, distanceValue11, valueInt, distanceValue13);
      handle(buffer, matrix, distanceValue5, distanceValue8, distanceValue5, amountForDouble, width2, deltaY, widthOffset, distanceValue10, distanceValue11, valueInt, distanceValue13);
      Arrays.fill(this.distanceTable, PearlGhost.getValue(PearlGhost.getValueForIntFloat(track.distanceValue, 0.25F), 0.9F * visibility));
      Arrays.fill(this.distanceColors, PearlGhost.getValue(track.distanceValue, 0.28F * visibility));
      float planeValue = Math.max(1.2F, distanceValue4 * 0.75F);
      float framebufferHeight = VectorPlaneUtils.getFramebufferHeight();
      double distanceValue15 = track.vecDirectionActive && framebufferHeight > 0.001F ? 0.5 * (planeValue + 1.25F) * (track.distanceTo + 0.5) / framebufferHeight : 0.0;
      double distanceValue16 = track.clampCount - 0.3F;
      double pearlPredictionValue = track.clampCount + 0.3F;
      double distanceValue18 = track.smoothedZ - 0.3F;
      double distanceValue19 = track.smoothedZ + 0.3F;
      double distanceValue20 = track.distanceCount + distanceValue15;
      double distanceValue21 = track.distanceCount + 1.8F;
      this.handle(matrix, camera, distanceValue16, distanceValue18, pearlPredictionValue, distanceValue19, distanceValue20, planeValue, distanceFlag);
      this.handle(matrix, camera, distanceValue16, distanceValue18, pearlPredictionValue, distanceValue19, distanceValue21, planeValue, distanceFlag);
      this.handleAmounts(matrix, camera, distanceValue16, distanceValue20, distanceValue18, distanceValue16, distanceValue21, distanceValue18, planeValue, distanceFlag);
      this.handleAmounts(matrix, camera, pearlPredictionValue, distanceValue20, distanceValue18, pearlPredictionValue, distanceValue21, distanceValue18, planeValue, distanceFlag);
      this.handleAmounts(matrix, camera, pearlPredictionValue, distanceValue20, distanceValue19, pearlPredictionValue, distanceValue21, distanceValue19, planeValue, distanceFlag);
      this.handleAmounts(matrix, camera, distanceValue16, distanceValue20, distanceValue19, distanceValue16, distanceValue21, distanceValue19, planeValue, distanceFlag);
   }

   @Override
   public void onRender2D(TickProgress tickProgress) {
      if (!this.pearlPredictionTrackList.isEmpty() && mc.world != null && mc.player != null) {
         if (mc.currentScreen == null && !mc.options.hudHidden) {
            DrawContext drawContext = tickProgress.drawContext;
            CorzClient.fontManager.setMatrixStack(this.matrixStack);
            MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
            double scaleFactor = mc.getWindow().getScaleFactor();
            int calculateScaleFactor = mc.getWindow().calculateScaleFactor(ShowHUD.getMax(mc), mc.forcesUnicodeFont());
            float pearlPredictionDivisor = (float)(calculateScaleFactor / scaleFactor);
            boolean pearlPredictionFlag = Math.abs(pearlPredictionDivisor - 1.0F) > 1.0E-4F;
            if (pearlPredictionFlag) {
               CorzRoundedUtils.scaledWidth = Math.round(mc.getWindow().getScaledWidth() / pearlPredictionDivisor);
               CorzRoundedUtils.scaledHeight = Math.round(mc.getWindow().getScaledHeight() / pearlPredictionDivisor);
            }

            try {
               this.pearlPredictionTrackList.sort((track, track2) -> Float.compare(track.directionVecAmount, track2.directionVecAmount));
               this.thicknessTrackList.clear();

               for (PearlPrediction.Track track3 : this.pearlPredictionTrackList) {
                  if (this.canAmountClamp(track3, pearlPredictionDivisor)) {
                     int pearlPredictionCounter = 0;
                     int itemsSize = this.thicknessTrackList.size();

                     while (true) {
                        if (pearlPredictionCounter < itemsSize) {
                           boolean pearlPredictionFlag2 = false;

                           for (int i = 0; i < itemsSize; i++) {
                              PearlPrediction.Track track4 = this.thicknessTrackList.get(i);
                              if (Math.abs(track4.clampAmount - track3.clampAmount) < (track4.rendererFont + track3.rendererFont) / 2.0F + 2.0F
                                 && track3.labelY < track4.labelY + track4.labelHeight + 2.0F
                                 && track4.labelY < track3.labelY + track3.labelHeight + 2.0F) {
                                 track3.labelY = track4.labelY + track4.labelHeight + 2.0F;
                                 pearlPredictionFlag2 = true;
                              }
                           }

                           if (pearlPredictionFlag2) {
                              pearlPredictionCounter++;
                              continue;
                           }
                        }

                        this.thicknessTrackList.add(track3);
                        break;
                     }
                  }
               }

               for (PearlPrediction.Track track5 : this.thicknessTrackList) {
                  this.renderAmountClamp(drawContext, track5);
               }
            } finally {
               if (pearlPredictionFlag) {
                  CorzRoundedUtils.scaledWidth = 0.0F;
                  CorzRoundedUtils.scaledHeight = 0.0F;
               }
            }
         }
      }
   }

   private void handleVecDirection(PearlPrediction.Track track) {
      track.vecDirectionActive = false;
      if (track.vecDirection.directionVecActive) {
         Vec3d vec3d = track.vecDirection.vec3d3VecMoved;
         track.x = vec3d.x;
         track.y = vec3d.y;
         track.z = vec3d.z;
         BlockHitResult raycast = mc.world
            .raycast(
               new RaycastContext(
                  new Vec3d(vec3d.x, vec3d.y + 0.05, vec3d.z),
                  new Vec3d(vec3d.x, vec3d.y - 2.5, vec3d.z),
                  ShapeType.COLLIDER,
                  FluidHandling.NONE,
                  ShapeContext.absent()
               )
            );
         if (raycast.getType() != Type.MISS) {
            track.y = raycast.getPos().y;
            track.vecDirectionActive = true;
         }
      }
   }

   private float getMax(PearlPrediction.Track track) {
      float pearlPredictionBound = 0.0F;
      var iterator = this.int2ObjectOpenHashMap.values().iterator();

      while (iterator.hasNext()) {
         PearlPrediction.Track pearlPredictionTrack = (PearlPrediction.Track)iterator.next();
         if (pearlPredictionTrack.trackVisible && pearlPredictionTrack.thicknessActive) {
            double deltaAmount = pearlPredictionTrack.clampCount - track.clampCount;
            double deltaAmount2 = pearlPredictionTrack.distanceCount - track.distanceCount;
            double deltaAmount3 = pearlPredictionTrack.smoothedZ - track.smoothedZ;
            if (deltaAmount * deltaAmount + deltaAmount2 * deltaAmount2 + deltaAmount3 * deltaAmount3 < 2.25) {
               pearlPredictionBound = Math.max(pearlPredictionBound, pearlPredictionTrack.fadeAlpha);
            }
         }
      }

      return pearlPredictionBound;
   }

   private void handlePartVisible(PearlPrediction.Track track, Entity entity, float partVisibleValue) {
      track.texturePath = null;
      if (entity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
         SkinTextures skin = abstractClientPlayerEntity.getSkin();
         if (skin != null && skin.body() != null) {
            track.texturePath = skin.body().texturePath();
            track.partVisibleActive = skin.model() == PlayerSkinType.SLIM;
            track.yaw = abstractClientPlayerEntity.getYaw(partVisibleValue);
            int value = 1365;
            if (abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.HAT)) {
               value |= 2;
            }

            if (abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.JACKET)) {
               value |= 8;
            }

            if (abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.RIGHT_SLEEVE)) {
               value |= 32;
            }

            if (abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.LEFT_SLEEVE)) {
               value |= 128;
            }

            if (abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.RIGHT_PANTS_LEG)) {
               value |= 512;
            }

            if (abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.LEFT_PANTS_LEG)) {
               value |= 2048;
            }

            track.partVisibleValue = value;
         }
      }
   }

   private void handleVecAmounts(PearlPrediction.Track track, double vecValue, double vecValue2, double vecValue3, boolean vecFlag) {
      Result vecDirection = track.vecDirection;
      var list = vecDirection.vecVec3dList;
      int value = 0;
      if (vecFlag) {
         value = getValue(track.vecTable, value, vecValue, vecValue2, vecValue3);
      }

      int vecLimit = vecDirection.directionVecActive ? list.size() - 1 : list.size();

      for (int i = 0; i < vecLimit; i++) {
         Vec3d vec3d = (Vec3d)list.get(i);
         value = getValue(track.vecTable, value, vec3d.x, vec3d.y, vec3d.z);
      }

      if (vecDirection.directionVecActive) {
         Vec3d vec3d2 = vecDirection.vec3d3VecMoved;
         int pearlPredictionValue = getValue(track.vecTable, value, vecDirection.centerPos.x, vecDirection.centerPos.y, vecDirection.centerPos.z);
         value = getValue(track.vecTable, pearlPredictionValue, 2.0 * vecDirection.centerPos.x - vec3d2.x, 2.0 * vecDirection.centerPos.y - vec3d2.y, 2.0 * vecDirection.centerPos.z - vec3d2.z);
         track.vecAmount = value - 3 + (vecDirection.exactFlightTicks - (vecDirection.flightTicks - 1));
      } else {
         track.vecAmount = value - 1;
      }

      track.pathPointCount = value;
   }

   private void renderAmountClamp(DrawContext context, PearlPrediction.Track track) {
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(track.visibility * track.thicknessAmount);

      try {
         PearlLabel.renderColorRenderer(
            context, track.clampAmount, track.labelY - 5.0F * track.clamp, track.clamp, track.pearlText, track.clampText, track.ageActive ? 0.0F : track.smoothedAge, 0xFF000000 | track.distanceValue
         );
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   private static int getValue(double[] pearlPredictionValues, int pearlPredictionValue, double vecValue, double pearlPredictionValue3, double pearlPredictionValue4) {
      pearlPredictionValues[pearlPredictionValue * 3] = vecValue;
      pearlPredictionValues[pearlPredictionValue * 3 + 1] = pearlPredictionValue3;
      pearlPredictionValues[pearlPredictionValue * 3 + 2] = pearlPredictionValue4;
      return pearlPredictionValue + 1;
   }

   private void handleAgeAmount(PearlPrediction.Track track, float ageValue) {
      ClientPlayerEntity player = mc.player;
      double lerp = MathHelper.lerp((double)ageValue, player.lastX, player.getX());
      double y = MathHelper.lerp((double)ageValue, player.lastY, player.getY()) + player.getStandingEyeHeight() - 0.1F;
      double lerp2 = MathHelper.lerp((double)ageValue, player.lastZ, player.getZ());
      double amount = player.getX() - player.lastX;
      double amount2 = player.isOnGround() ? 0.0 : player.getY() - player.lastY;
      double amount3 = player.getZ() - player.lastZ;
      if (player.age != this.age) {
         boolean ageFlag = player.age == this.age + 1;
         this.pearlPredictionAmount = ageFlag ? this.ageAmount : amount;
         this.targetPosY = ageFlag ? this.lastPosY : amount2;
         this.targetPosZ = ageFlag ? this.lastPosZ : amount3;
         this.ageAmount = amount;
         this.lastPosY = amount2;
         this.lastPosZ = amount3;
         this.age = player.age;
      }

      double ageFactor = 1.0 - ageValue;
      Vec3d add = FloorMathUtilsDirection.getAdd(player)
         .add((this.pearlPredictionAmount - this.ageAmount) * ageFactor, (this.targetPosY - this.lastPosY) * ageFactor, (this.targetPosZ - this.lastPosZ) * ageFactor);
      track.trackAlive = true;
      track.ageActive = true;
      track.directionVecActive = false;
      if (track.vecDirection == null
         || track.age != player.age
         || track.entityPos == null
         || track.entityPos.x != lerp
         || track.entityPos.y != y
         || track.entityPos.z != lerp2
         || !add.equals(track.add)) {
         track.entityPos = new Vec3d(lerp, y, lerp2);
         track.add = add;
         track.age = player.age;
         track.vecDirection = FloorMathUtilsDirection.getBoundingBox(track.entityPos, add, false, 0, null, player, true, 300);
         this.handleVecAmounts(track, 0.0, 0.0, 0.0, false);
         this.handleVecDirection(track);
      }

      track.pearlText = "";
      this.handlePartVisible(track, player, ageValue);
      track.distanceValue = MatrixEspUtils.releaseRightValue & 16777215;
      track.pathStartDistance = 0.0;
      track.ageAmount = 0.0F;
      track.directionVecAmount = track.vecDirection.directionVecActive ? (float)(track.vecDirection.exactFlightTicks / 20.0) : 0.0F;
      track.firstPersonView = mc.options.getPerspective().isFirstPerson() && !Freecam.isInstanceFlag() && !ClampMatrixUtils.isFrameCamera();
      track.planeAmount = player.getMainArm() == Arm.RIGHT == player.getMainHandStack().isOf(Items.ENDER_PEARL) ? -1.0F : 1.0F;
      track.trackVisible = track.vecDirection.directionVecActive && track.vecDirection.vec3d3VecMoved.squaredDistanceTo(player.getX(), player.getY(), player.getZ()) > 6.25;
   }

   @Override
   public void onDisable() {
      this.updateTrackThickness();
   }

   private void handleDirectionVec(PearlPrediction.Track track, EnderPearlEntity enderPearlEntity, Entity entity, float amount) {
      track.trackAlive = true;
      track.ageActive = false;
      track.firstPersonView = false;
      Vec3d entityPos = enderPearlEntity.getEntityPos();
      Vec3d add = enderPearlEntity.getVelocity();
      if (track.vecDirection == null || track.entityPos != entityPos || track.add != add) {
         if (this.canDirectionVec(track, enderPearlEntity, entityPos)) {
            track.directionVecActive = true;
         } else {
            track.directionVecActive = false;
            track.vecDirection = FloorMathUtilsDirection.getTouchingOwner(enderPearlEntity, true, 300);
            track.directionVecValue = enderPearlEntity.age;
            this.handleVecAmounts(track, enderPearlEntity.lastX, enderPearlEntity.lastY, enderPearlEntity.lastZ, true);
            this.handleVecDirection(track);
         }

         track.entityPos = entityPos;
         track.add = add;
      }

      int directionVecValue = entity == null ? -1 : entity.getId();
      if (directionVecValue != track.pearlEntityId) {
         track.pearlEntityId = directionVecValue;
         track.pearlText = entity == null ? "Pearl" : NameProtect.getString(entity.getName().getString());
      }

      this.handlePartVisible(track, entity, amount);
      track.distanceValue = (entity == mc.player ? MatrixEspUtils.releaseRightValue : (entity != null && FriendNameStore.canFlag(entity) ? -8393375 : -1750963)) & 16777215;
      track.trackVisible = track.vecDirection.directionVecActive && !(entity instanceof LivingEntity livingEntity && !livingEntity.isAlive());
      if (track.directionVecActive) {
         track.pathStartDistance = track.vecAmount;
         track.ageAmount = 1.0F;
         track.directionVecAmount = 0.0F;
      } else {
         track.pathStartDistance = amount;
         track.ageAmount = track.vecDirection.directionVecActive ? PearlMath.getAmount(enderPearlEntity.age, amount, track.vecDirection.flightTicks) : 0.0F;
         track.directionVecAmount = track.vecDirection.directionVecActive ? PearlMath.getAmount(track.vecDirection.flightTicks, amount) : 0.0F;
      }
   }

   private void updateTrackThickness() {
      this.int2ObjectOpenHashMap.clear();
      this.pearlPredictionTrackList.clear();
      this.thicknessTrackList.clear();
      this.flightThicknessItems.clear();
      this.track.vecDirection = null;
      this.track.entityPos = null;
      this.track.add = null;
      this.track.texturePath = null;
      this.track.distanceAmount = 0.0F;
      this.track.thicknessAmount = 0.0F;
      this.track.thicknessActive = false;
      this.age = Integer.MIN_VALUE;
      this.lastNanoTime = 0L;
   }

   private static final class Track {
      boolean directionVecActive;
      boolean distanceActive;
      String clampText;
      float fadeAlpha;
      float yaw;
      double x;
      float directionVecAmount;
      float clampAmount;
      float labelHeight;
      boolean ageActive;
      int pathPointCount;
      double distanceTo;
      int distanceValue;
      Vec3d entityPos;
      int age;
      int directionVecValue;
      String pearlText;
      double distanceCount;
      Vec3d add;
      Identifier texturePath;
      double vecAmount;
      boolean thicknessActive;
      int clampValue;
      boolean firstPersonView;
      double y;
      float ageAmount;
      float distanceAmount;
      int partVisibleValue;
      float labelY;
      Result vecDirection;
      double z;
      double clampCount;
      final double[] vecTable = new double[912];
      float smoothedAge;
      float visibility;
      float rendererFont;
      boolean trackVisible;
      int pearlEntityId;
      boolean vecDirectionActive;
      double pathStartDistance;
      float thicknessAmount;
      float clamp;
      boolean partVisibleActive;
      boolean trackAlive;
      float planeAmount;
      double smoothedZ;

      private Track() {
         this.pearlText = "";
         this.clampText = "";
         this.clampValue = -1;
         this.age = -1;
         this.pearlEntityId = Integer.MIN_VALUE;
      }
   }
}
