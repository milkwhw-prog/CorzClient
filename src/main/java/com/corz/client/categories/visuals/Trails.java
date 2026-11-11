package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.utils.TrailMesher.Mode;
import com.corz.client.utils.TrailMesher.Style;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.utils.TrailMesher;
import ai.auth.Module;
import com.corz.client.utils.TrailBuffer;
import com.corz.client.utils.TrailSink;
import com.corz.client.utils.IconsPngType;

public class Trails extends Module {
   private final DoubleSetting doubleHelper;
   private final Style style;
   private long frameCounter;
   private final TrailMesher trailMesher;
   private PlayerEntity player;
   private double x;
   private final TrailBuffer trailBuffer;
   private final ColorSetting colorBase;
   private boolean worldTrailActive;
   private final DoubleSetting fadeStyleTheme;
   private final BooleanSetting booleanBase;
   private final EnumSetting enumHelper;
   private final EnumSetting modeStyleTheme = new EnumSetting(
      "Mode",
      "Ribbon: a glowing curtain from your feet up to Height. Line: a glowing light streak at Height. Dots: glowing beads dropped along your path.",
      "Ribbon",
      "Ribbon",
      "Line",
      "Dots"
   );
   private final ArrayList<TrailBuffer> drawPersonItems;
   private final ArrayDeque<TrailBuffer> arrayDeque;
   private ClientWorld world;
   private double y;
   private final TrailBuffer[] trailBuffers;
   private final DoubleSetting widthStyleTheme;
   private int trackedPlayerCount;
   private final Trails.QueueSink queueSink;
   private final DoubleSetting length;
   private final AbstractClientPlayerEntity[] abstractClientPlayerEntis;
   private final double[] entisAbstractTable;
   private static final long lastNanoTime = System.nanoTime();
   private final boolean[] buffersTrailFlags;
   private double z;
   private final BooleanSetting hideInFirstPerson;

   private void updateBuffersTrail() {
      for (int i = 0; i < this.trackedPlayerCount; i++) {
         TrailBuffer personDraw = this.getPersonDraw(this.abstractClientPlayerEntis[i].getId());
         this.trailBuffers[i] = personDraw;
         this.buffersTrailFlags[i] = false;
         if (personDraw != null) {
            this.buffersTrailFlags[i] = personDraw.lastUpdateFrame != this.frameCounter - 1L;
            personDraw.lastUpdateFrame = this.frameCounter;
         }
      }

      for (int j = 0; j < this.trackedPlayerCount; j++) {
         if (this.trailBuffers[j] == null) {
            TrailBuffer pop = this.getPop();
            if (pop != null) {
               pop.updateFlag();
               pop.entityId = this.abstractClientPlayerEntis[j].getId();
               pop.lastUpdateFrame = this.frameCounter;
               this.trailBuffers[j] = pop;
            }
         }
      }
   }

   private TrailBuffer getPersonDraw(int personDrawValue) {
      for (int i = 0; i < this.drawPersonItems.size(); i++) {
         TrailBuffer drawpersonitem = this.drawPersonItems.get(i);
         if (drawpersonitem.entityId == personDrawValue) {
            return drawpersonitem;
         }
      }

      return null;
   }

   private TrailBuffer getPop() {
      if (this.drawPersonItems.size() < 20) {
         TrailBuffer pop = this.arrayDeque.isEmpty() ? new TrailBuffer() : this.arrayDeque.pop();
         this.drawPersonItems.add(pop);
         return pop;
      } else {
         TrailBuffer drawpersonitem2 = null;

         for (int i = 0; i < this.drawPersonItems.size(); i++) {
            TrailBuffer drawpersonitem = this.drawPersonItems.get(i);
            if (drawpersonitem.lastUpdateFrame != this.frameCounter && (drawpersonitem2 == null || drawpersonitem.lastUpdateFrame < drawpersonitem2.lastUpdateFrame)) {
               drawpersonitem2 = drawpersonitem;
            }
         }

         return drawpersonitem2;
      }
   }

   private static void handleLerpHeight(TrailBuffer trailBuffer, Entity entity, float lerpHeightValue, double heightValue, double trailBufferBound) {
      double lerp = MathHelper.lerp((double)lerpHeightValue, entity.lastX, entity.getX());
      double lerp2 = MathHelper.lerp((double)lerpHeightValue, entity.lastY, entity.getY());
      double lerp3 = MathHelper.lerp((double)lerpHeightValue, entity.lastZ, entity.getZ());
      trailBuffer.handleAmountsHeight(heightValue, lerp, lerp2, lerp3, entity.getHeight(), trailBufferBound);
   }

   private void updateWorldTrail() {
      if (mc.world != this.world) {
         this.trailBuffer.updateFlag();
         this.updatePersonDraw();
         this.worldTrailActive = false;
         this.world = mc.world;
      }

      if (mc.player != this.player) {
         this.trailBuffer.updateFlag();
         this.worldTrailActive = false;
         this.player = mc.player;
      }
   }

   @Override
   protected void onReset() {
      if (mc.world == null && this.world != null) {
         this.updateTrailBuffer();
      }

      if (mc.player != null && mc.world != null) {
         this.updateWorldTrail();
         double x = mc.player.getX();
         double y = mc.player.getY();
         double z = mc.player.getZ();
         if (this.worldTrailActive) {
            double iIiFactor = x - this.x;
            double iIiFactor2 = y - this.y;
            double iIiFactor3 = z - this.z;
            if (iIiFactor * iIiFactor + iIiFactor2 * iIiFactor2 + iIiFactor3 * iIiFactor3 > 64.0) {
               this.trailBuffer.updateFlagHeightActive();
            }
         }

         this.x = x;
         this.y = y;
         this.z = z;
         this.worldTrailActive = true;
      }
   }

   private void updatePersonDraw() {
      for (int i = 0; i < this.drawPersonItems.size(); i++) {
         TrailBuffer drawpersonitem = this.drawPersonItems.get(i);
         drawpersonitem.updateFlag();
         drawpersonitem.entityId = Integer.MIN_VALUE;
         this.arrayDeque.push(drawpersonitem);
      }

      this.drawPersonItems.clear();
   }

   private void updateEntisAbstract() {
      this.trackedPlayerCount = 0;
      var players = mc.world.getPlayers();
      int index = 0;

      for (int i = players.size(); index < i; index++) {
         AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)players.get(index);
         if (abstractClientPlayerEntity != mc.player && !abstractClientPlayerEntity.isSpectator() && !abstractClientPlayerEntity.isInvisibleTo(mc.player)) {
            double squaredDistanceTo = abstractClientPlayerEntity.squaredDistanceTo(mc.player);
            if (!(squaredDistanceTo > 9216.0)) {
               int trackedPlayerCount2;
               if (this.trackedPlayerCount < 16) {
                  trackedPlayerCount2 = this.trackedPlayerCount++;
               } else {
                  if (squaredDistanceTo >= this.entisAbstractTable[15]) {
                     continue;
                  }

                  trackedPlayerCount2 = 15;
               }

               while (trackedPlayerCount2 > 0 && this.entisAbstractTable[trackedPlayerCount2 - 1] > squaredDistanceTo) {
                  this.abstractClientPlayerEntis[trackedPlayerCount2] = this.abstractClientPlayerEntis[trackedPlayerCount2 - 1];
                  this.entisAbstractTable[trackedPlayerCount2] = this.entisAbstractTable[trackedPlayerCount2 - 1];
                  trackedPlayerCount2--;
               }

               this.abstractClientPlayerEntis[trackedPlayerCount2] = abstractClientPlayerEntity;
               this.entisAbstractTable[trackedPlayerCount2] = squaredDistanceTo;
            }
         }
      }
   }

   private void updateTrailBuffer() {
      this.trailBuffer.updateFlag();
      this.updatePersonDraw();
      this.world = null;
      this.player = null;
      this.worldTrailActive = false;
   }

   private void handleStyleTheme(double amount) {
      Style style = this.style;
      String formatText = this.modeStyleTheme.getCurrentOption();

      style.mode = switch (formatText) {
         case "Line" -> Mode.LINE;
         case "Dots" -> Mode.DOTS;
         default -> Mode.RIBBON;
      };
      String currentOption = this.enumHelper.getCurrentOption();
      this.style.equals = currentOption.equals("Rainbow");
      if (currentOption.equals("Custom")) {
         int intValue = this.colorBase.getColor();
         this.style.themeCustomValue = intValue & 16777215;
         this.style.themeAmount = (intValue >>> 24 & 0xFF) / 255.0F;
      } else {
         this.style.themeCustomValue = (currentOption.equals("Theme") ? MatrixEspUtils.releaseRightValue : this.colorBase.getColor()) & 16777215;
         this.style.themeAmount = 1.0F;
      }

      this.style.trailLength = this.length.getValue();
      this.style.styleAmount = (float)this.widthStyleTheme.getValue();
      this.style.widthScale = (float)this.doubleHelper.getValue();
      this.style.themeAlpha = (float)this.fadeStyleTheme.getValue();
      this.style.styleThemeAmount = amount;
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.player != null && mc.world != null) {
         this.updateWorldTrail();
         Camera camera = mc.gameRenderer.getCamera();
         if (camera != null) {
            Vec3d cameraPos = camera.getCameraPos();
            double seconds = (System.nanoTime() - lastNanoTime) / 1.0E9;
            double trailsValue = this.length.getValue();
            float tickProgress = queueCommand.tickProgress;
            this.frameCounter++;
            this.handleStyleTheme(seconds);
            this.queueSink.handleConsumerVertex(queueCommand.matrices.peek().getPositionMatrix());
            handleLerpHeight(this.trailBuffer, mc.player, tickProgress, seconds, trailsValue);
            this.trailBuffer.handleAmountsFlags(seconds, trailsValue);
            boolean trailsFlag = this.hideInFirstPerson.isEnabled()
               && mc.options.getPerspective().isFirstPerson()
               && !Freecam.isInstanceFlag()
               && !ClampMatrixUtils.isFrameCamera();
            this.style.updateClampAmount();
            if (!trailsFlag) {
               this.trailMesher.handleAmountSink(this.trailBuffer, seconds, cameraPos.x, cameraPos.y, cameraPos.z, this.style, this.queueSink);
            }

            if (this.booleanBase.isEnabled()) {
               this.updateEntisAbstract();
               this.updateBuffersTrail();

               for (int i = 0; i < this.trackedPlayerCount; i++) {
                  TrailBuffer trailBuffer = this.trailBuffers[i];
                  if (trailBuffer != null) {
                     if (this.buffersTrailFlags[i] && trailBuffer.isFlag()) {
                        trailBuffer.updateFlagHeightActive();
                     }

                     handleLerpHeight(trailBuffer, this.abstractClientPlayerEntis[i], tickProgress, seconds, trailsValue);
                  }

                  this.abstractClientPlayerEntis[i] = null;
                  this.trailBuffers[i] = null;
               }

               int trailsDivisor = Math.max(1, this.drawPersonItems.size());
               int clamp = MathHelper.clamp(3200 / trailsDivisor, 64, this.trailMesher.getSinkValue());
               int clamp2 = MathHelper.clamp(1600 / trailsDivisor, 24, 400);

               for (int j = this.drawPersonItems.size() - 1; j >= 0; j--) {
                  TrailBuffer drawpersonitem = this.drawPersonItems.get(j);
                  drawpersonitem.handleAmountsFlags(seconds, trailsValue);
                  if (drawpersonitem.lastUpdateFrame != this.frameCounter && drawpersonitem.canLerpAmounts(seconds, trailsValue)) {
                     int trailsValue2 = this.drawPersonItems.size() - 1;
                     this.drawPersonItems.set(j, this.drawPersonItems.get(trailsValue2));
                     this.drawPersonItems.remove(trailsValue2);
                     drawpersonitem.updateFlag();
                     drawpersonitem.entityId = Integer.MIN_VALUE;
                     this.arrayDeque.push(drawpersonitem);
                  } else {
                     double trailsFactor = drawpersonitem.getLerp() - cameraPos.x;
                     double trailsFactor2 = drawpersonitem.getLerp2LerpHeight() - cameraPos.y;
                     double trailsFactor3 = drawpersonitem.getLerp3LerpHeight() - cameraPos.z;
                     double distance = Math.sqrt(trailsFactor * trailsFactor + trailsFactor2 * trailsFactor2 + trailsFactor3 * trailsFactor3);
                     this.style.clamp = (float)MathHelper.clamp(distance / 16.0, 1.0, 4.0);
                     this.style.clampAmount = TrailMesher.getAmount2(20.0F, 28.0F, (float)distance);
                     this.style.clampSink = clamp;
                     this.style.clamp2Clamp = clamp2;
                     this.trailMesher.handleAmountSink(drawpersonitem, seconds, cameraPos.x, cameraPos.y, cameraPos.z, this.style, this.queueSink);
                  }
               }

               this.style.updateClampAmount();
            } else if (!this.drawPersonItems.isEmpty()) {
               this.updatePersonDraw();
            }

            this.queueSink.updateConsumerVertex();
         }
      }
   }

   @Override
   protected void onDisable() {
      this.updateTrailBuffer();
   }

   public Trails() {
      super("Trails", IconsPngType.VISUAL);
      this.enumHelper = new EnumSetting(
         "Color Mode",
         "Custom uses Color, Rainbow flows through the hues along the trail, Theme follows the client accent.",
         "Theme",
         "Custom",
         "Rainbow",
         "Theme"
      );
      this.colorBase = new ColorSetting("Color", "Trail colour in Custom mode (its alpha sets the opacity)", -5204993);
      this.length = new DoubleSetting("Length", "How long the trail lingers behind you (seconds)", 1.5, 0.3, 5.0, 0.1);
      this.widthStyleTheme = new DoubleSetting("Width", "Glow width of the line, the ribbon edges and the dots (blocks)", 0.3, 0.05, 1.0, 0.01);
      this.doubleHelper = new DoubleSetting(
         "Height", "Where on the body the trail sits, 0 = feet and 1 = head (the Ribbon reaches from the feet up to here)", 1.0, 0.0, 1.0, 0.05
      );
      this.fadeStyleTheme = new DoubleSetting("Fade", "How strongly the trail fades out towards its tail", 1.0, 0.0, 1.0, 0.05);
      this.hideInFirstPerson = new BooleanSetting("Hide In First Person", "Don't draw your own trail in first person, so it never smears across your view", true);
      this.booleanBase = new BooleanSetting("Others", "Also draw trails behind other players", false);
      this.trailBuffer = new TrailBuffer();
      this.drawPersonItems = new ArrayList<>(16);
      this.arrayDeque = new ArrayDeque<>();
      this.trailMesher = new TrailMesher();
      this.style = new Style();
      this.queueSink = new Trails.QueueSink();
      this.abstractClientPlayerEntis = new AbstractClientPlayerEntity[16];
      this.entisAbstractTable = new double[16];
      this.trailBuffers = new TrailBuffer[16];
      this.buffersTrailFlags = new boolean[16];
      this.addSettings(
         new ConfigSetting[]{
            this.modeStyleTheme,
            this.enumHelper,
            this.colorBase,
            this.length,
            this.widthStyleTheme,
            this.doubleHelper,
            this.fadeStyleTheme,
            this.hideInFirstPerson,
            this.booleanBase
         }
      );
   }

   @Override
   protected void onEnable() {
      this.updateTrailBuffer();
   }

   private static final class QueueSink implements TrailSink {
      private VertexConsumer vertexConsumer;
      private VertexConsumer buffer;
      private Matrix4f positionMatrix;

      @Override
      public void handleConsumerVertex(
         float consumerVertexValue, float consumerVertexValue2, float consumerVertexValue3, int valueInt, float consumerVertexValue5, float consumerVertexValue6, float consumerVertexValue7, int consumerVertexValue8, float consumerVertexValue9, float deltaY, float consumerVertexValue11, int consumerVertexValue12, float amountX, float consumerVertexValue14, float amountForDouble, int consumerVertexValue16
      ) {
         if (this.vertexConsumer == null) {
            this.vertexConsumer = VertexBuilderUtils.getBuffer();
         }

         VertexBuilderUtils.handleVertexColor(this.vertexConsumer, this.positionMatrix, consumerVertexValue, consumerVertexValue2, consumerVertexValue3, valueInt, consumerVertexValue5, consumerVertexValue6, consumerVertexValue7, consumerVertexValue8, consumerVertexValue9, deltaY, consumerVertexValue11, consumerVertexValue12, amountX, consumerVertexValue14, amountForDouble, consumerVertexValue16);
      }

      void handleConsumerVertex(Matrix4f positionMatrix) {
         this.positionMatrix = positionMatrix;
         this.vertexConsumer = null;
         this.buffer = null;
      }

      @Override
      public void handleConsumerVertex(float consumerVertexValue, float consumerVertexValue2, float vc2, int valueForIntFloat, float consumerVertexValue5, float consumerVertexValue6, float consumerVertexValue7, int consumerVertexValue8, float consumerVertexValue9, float consumerVertexValue10, float consumerVertexValue11, int consumerVertexValue12) {
         if (this.buffer == null) {
            this.buffer = VertexBuilderUtils.getBuffer5();
         }

         VertexBuilderUtils.handleVertexColor(this.buffer, this.positionMatrix, consumerVertexValue, consumerVertexValue2, vc2, valueForIntFloat, consumerVertexValue5, consumerVertexValue6, consumerVertexValue7, consumerVertexValue8, consumerVertexValue9, consumerVertexValue10, consumerVertexValue11, consumerVertexValue12);
      }

      void updateConsumerVertex() {
         this.vertexConsumer = null;
         this.buffer = null;
         this.positionMatrix = null;
      }
   }
}
