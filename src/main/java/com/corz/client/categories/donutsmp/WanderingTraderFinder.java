package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorDotUtils;
import com.corz.client.render.VertexBuilderUtils;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.PingSoundPlayer;
import com.corz.client.utils.IconsPngType;

public class WanderingTraderFinder extends Module {
   private final List<WanderingTraderEntity> traders;
   private final BooleanSetting heatDetection;
   private final BooleanSetting soundPing;
   private int scanTickCounter;
   private final BooleanSetting alerts = new BooleanSetting("Alerts", "Notify on trader detection", true);
   private final BooleanSetting autoWaypoint;
   private final BooleanSetting tracerTrader;
   private final IntegerSetting alertCooldownSeconds;
   private final Map<Integer, Long> stationarySinceMsById;
   private final BooleanSetting tracers;
   private final BooleanSetting renderEsp;
   private final IntegerSetting heatRadius;
   private final BooleanSetting tracerLlamas;
   private final List<TraderLlamaEntity> nearbyLlamas;
   private final BooleanSetting llamaEsp;
   private final DoubleSetting tracerAlpha;
   private final Map<Integer, Vec3d> stationaryPosById;
   private final Set<Integer> alertedTraderIds;
   private final DoubleSetting tracerWidth;
   private final IntegerSetting llamaRadius;
   private final Map<Integer, Long> lastAlertMsById;
   private final BooleanSetting labels;

   private Color getEspColor() {
      Color color = new Color(MatrixEspUtils.releaseRightValue);
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   private void resetState() {
      this.alertedTraderIds.clear();
      this.lastAlertMsById.clear();
      this.stationarySinceMsById.clear();
      this.stationaryPosById.clear();
      this.traders.clear();
      this.nearbyLlamas.clear();
      this.scanTickCounter = 0;
   }

   private void alertTraderDetected(WanderingTraderEntity wanderingTraderEntity) {
      if (this.alerts.isEnabled()) {
         int id = wanderingTraderEntity.getId();
         long currentMillis = System.currentTimeMillis();
         Long filelogmap = this.lastAlertMsById.get(id);
         if (filelogmap == null || currentMillis - filelogmap >= this.alertCooldownSeconds.getValue() * 1000L) {
            if (!this.alertedTraderIds.contains(id) || filelogmap == null) {
               this.alertedTraderIds.add(id);
               this.lastAlertMsById.put(id, currentMillis);
               int filePossibleValue = MathHelper.floor(wanderingTraderEntity.getX());
               int floor = MathHelper.floor(wanderingTraderEntity.getY());
               int floor3 = MathHelper.floor(wanderingTraderEntity.getZ());
               int distanceTo = (int)mc.player.distanceTo(wanderingTraderEntity);
               String format = String.format("Possible base nearby! X:%d Y:%d Z:%d (%dm)", filePossibleValue, floor, floor3, distanceTo);
               DisabledEnabled.getInstance().handleDisabledEnabled("Wandering Trader", format, true);
               if (this.soundPing.isEnabled()) {
                  PingSoundPlayer.playPingSound();
               }

               if (this.autoWaypoint.isEnabled()) {
                  this.appendTraderLogEntry(wanderingTraderEntity, filePossibleValue, floor, floor3, distanceTo);
               }
            }
         }
      }
   }

   private Color getLlamaEspColor() {
      Color color = new Color(MatrixEspUtils.releaseRightValue);
      return new Color((int)(color.getRed() * 0.65F), (int)(color.getGreen() * 0.65F), (int)(color.getBlue() * 0.65F), 200);
   }

   public WanderingTraderFinder() {
      super("TraderFinder", IconsPngType.DONUTSMP);
      this.soundPing = new BooleanSetting("Sound Ping", "Play sci-fi beep on detection", true);
      this.alertCooldownSeconds = new IntegerSetting("Alert Cooldown", "Seconds before re-alert for same trader", 60, 10, 300);
      this.renderEsp = new BooleanSetting("Render ESP", "Draw ESP on traders", true);
      this.llamaEsp = new BooleanSetting("Llama ESP", "Draw ESP on trader llamas", true);
      this.labels = new BooleanSetting("Labels", "Show name + heat labels", true);
      this.tracers = new BooleanSetting("Tracers", "Draw tracer lines to entities", true);
      this.tracerTrader = new BooleanSetting("Tracer Trader", "Tracer to Wandering Trader", true);
      this.tracerLlamas = new BooleanSetting("Tracer Llamas", "Tracer to Trader Llamas", true);
      this.tracerAlpha = new DoubleSetting("Tracer Alpha", "Tracer line opacity", 0.7, 0.1, 1.0, 0.05);
      this.tracerWidth = new DoubleSetting("Tracer Width", "Tracer line thickness", 1.5, 0.5, 4.0, 0.5);
      this.heatDetection = new BooleanSetting("Heat Detection", "Track trader stationary time", true);
      this.heatRadius = new IntegerSetting("Heat Radius", "Max movement for stationary detection", 12, 5, 30);
      this.llamaRadius = new IntegerSetting("Llama Radius", "Max distance to link llamas", 20, 5, 50);
      this.autoWaypoint = new BooleanSetting("Auto Waypoint", "Log detection to file", true);
      this.alertedTraderIds = new HashSet<>();
      this.lastAlertMsById = new HashMap<>();
      this.stationarySinceMsById = new HashMap<>();
      this.stationaryPosById = new HashMap<>();
      this.traders = new ArrayList<>();
      this.nearbyLlamas = new ArrayList<>();
      this.scanTickCounter = 0;
      this.addSettings(
         new ConfigSetting[]{
            this.alerts,
            this.soundPing,
            this.alertCooldownSeconds,
            this.renderEsp,
            this.llamaEsp,
            this.labels,
            this.tracers,
            this.tracerTrader,
            this.tracerLlamas,
            this.tracerAlpha,
            this.tracerWidth,
            this.heatDetection,
            this.heatRadius,
            this.llamaRadius,
            this.autoWaypoint
         }
      );
   }

   private void renderBoxOutline(MatrixStack matrices, float peekPositionValue, float deltaY, float amountForDouble, float wanderingTraderValue, float peekPositionValue5, float peekPositionValue6, Color color) {
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      int rgb = color.getRGB();
      float peekPositionValue7 = (wanderingTraderValue - peekPositionValue) * 0.25F;
      float peekPositionValue8 = (peekPositionValue5 - deltaY) * 0.25F;
      float peekPositionValue9 = (peekPositionValue6 - amountForDouble) * 0.25F;
      this.drawCornerEdges(positionMatrix, peekPositionValue, deltaY, amountForDouble, peekPositionValue7, peekPositionValue8, peekPositionValue9, rgb);
      this.drawCornerEdges(positionMatrix, wanderingTraderValue, deltaY, amountForDouble, -peekPositionValue7, peekPositionValue8, peekPositionValue9, rgb);
      this.drawCornerEdges(positionMatrix, wanderingTraderValue, deltaY, peekPositionValue6, -peekPositionValue7, peekPositionValue8, -peekPositionValue9, rgb);
      this.drawCornerEdges(positionMatrix, peekPositionValue, deltaY, peekPositionValue6, peekPositionValue7, peekPositionValue8, -peekPositionValue9, rgb);
      this.drawCornerEdges(positionMatrix, peekPositionValue, peekPositionValue5, amountForDouble, peekPositionValue7, -peekPositionValue8, peekPositionValue9, rgb);
      this.drawCornerEdges(positionMatrix, wanderingTraderValue, peekPositionValue5, amountForDouble, -peekPositionValue7, -peekPositionValue8, peekPositionValue9, rgb);
      this.drawCornerEdges(positionMatrix, wanderingTraderValue, peekPositionValue5, peekPositionValue6, -peekPositionValue7, -peekPositionValue8, -peekPositionValue9, rgb);
      this.drawCornerEdges(positionMatrix, peekPositionValue, peekPositionValue5, peekPositionValue6, peekPositionValue7, -peekPositionValue8, -peekPositionValue9, rgb);
      VertexBuilderUtils.updateDrawImmediate();
   }

   private void renderEntityBox(MatrixStack matrices, Vec3d vec, Entity entity, float lerpWidthValue, Color color) {
      double lerp = MathHelper.lerp((double)lerpWidthValue, entity.lastX, entity.getX());
      double lerp2 = MathHelper.lerp((double)lerpWidthValue, entity.lastY, entity.getY());
      double lerp3 = MathHelper.lerp((double)lerpWidthValue, entity.lastZ, entity.getZ());
      float widthScaled = entity.getWidth() / 2.0F;
      float height = entity.getHeight();
      float peekPositionValue = (float)(lerp - widthScaled - vec.x);
      float lerpWidthValue3 = (float)(lerp2 - vec.y);
      float lerpWidthValue4 = (float)(lerp3 - widthScaled - vec.z);
      float wanderingTraderValue = (float)(lerp + widthScaled - vec.x);
      float deltaY = (float)(lerp2 + height - vec.y);
      float amountForDouble = (float)(lerp3 + widthScaled - vec.z);
      this.renderBoxOutline(matrices, peekPositionValue, lerpWidthValue3, lerpWidthValue4, wanderingTraderValue, deltaY, amountForDouble, color);
   }

   private void drawCornerEdges(Matrix4f matrix, float wanderingTraderValue, float deltaY, float amountForDouble, float wanderingTraderValue4, float wanderingTraderValue5, float wanderingTraderValue6, int rgb) {
      VertexBuilderUtils.handleForMatrix4fFloat(matrix, wanderingTraderValue, deltaY, amountForDouble, wanderingTraderValue + wanderingTraderValue4, deltaY, amountForDouble, rgb, 2.0F);
      VertexBuilderUtils.handleForMatrix4fFloat(matrix, wanderingTraderValue, deltaY, amountForDouble, wanderingTraderValue, deltaY + wanderingTraderValue5, amountForDouble, rgb, 2.0F);
      VertexBuilderUtils.handleForMatrix4fFloat(matrix, wanderingTraderValue, deltaY, amountForDouble, wanderingTraderValue, deltaY, amountForDouble + wanderingTraderValue6, rgb, 2.0F);
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null) {
         PingSoundPlayer.noOpUpdate();
         this.scanTickCounter++;
         if (this.scanTickCounter % 8 == 0) {
            this.traders.clear();
            this.nearbyLlamas.clear();

            for (Entity entity : mc.world.getEntities()) {
               if (entity instanceof WanderingTraderEntity wanderingTraderEntity && wanderingTraderEntity.isAlive() && !wanderingTraderEntity.isRemoved()) {
                  this.traders.add(wanderingTraderEntity);
               }
            }

            double iIiLimit = this.llamaRadius.getValue() * this.llamaRadius.getValue();

            for (WanderingTraderEntity wanderingTraderEntity2 : this.traders) {
               for (Entity getEntitiesItem : mc.world.getEntities()) {
                  if (getEntitiesItem instanceof TraderLlamaEntity traderLlamaEntity
                     && traderLlamaEntity.isAlive()
                     && !traderLlamaEntity.isRemoved()
                     && traderLlamaEntity.squaredDistanceTo(wanderingTraderEntity2) <= iIiLimit
                     && !this.nearbyLlamas.contains(traderLlamaEntity)) {
                     this.nearbyLlamas.add(traderLlamaEntity);
                  }
               }

               this.alertTraderDetected(wanderingTraderEntity2);
               if (this.heatDetection.isEnabled()) {
                  this.trackTraderMovement(wanderingTraderEntity2);
               }
            }

            var hashSet = new HashSet();

            for (WanderingTraderEntity wanderingTraderEntity3 : this.traders) {
               hashSet.add(wanderingTraderEntity3.getId());
            }

            this.stationarySinceMsById.keySet().retainAll(hashSet);
            this.stationaryPosById.keySet().retainAll(hashSet);
         }
      }
   }

   private void appendTraderLogEntry(WanderingTraderEntity wanderingTraderEntity, int traderfinderWanderingValue, int traderfinderWanderingValue2, int traderfinderWanderingValue3, int distanceTo) {
      try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("traderfinder_log.txt", true))) {
         String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
         bufferedWriter.write(String.format("[%s] Wandering Trader | X:%d Y:%d Z:%d | Dist:%d | ID:%d%n", format, traderfinderWanderingValue, traderfinderWanderingValue2, traderfinderWanderingValue3, distanceTo, wanderingTraderEntity.getId()));
      } catch (IOException e) {
      }
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }

   private void trackTraderMovement(WanderingTraderEntity wanderingTraderEntity) {
      int id = wanderingTraderEntity.getId();
      Vec3d entityPos = wanderingTraderEntity.getEntityPos();
      double logFileValue = this.heatRadius.getValue();
      if (!this.stationarySinceMsById.containsKey(id)) {
         this.stationarySinceMsById.put(id, System.currentTimeMillis());
         this.stationaryPosById.put(id, entityPos);
      } else {
         Vec3d filelogmap = this.stationaryPosById.get(id);
         if (filelogmap.distanceTo(entityPos) > logFileValue) {
            this.stationarySinceMsById.put(id, System.currentTimeMillis());
            this.stationaryPosById.put(id, entityPos);
         }
      }
   }

   private void renderEntityTracer(MatrixStack matrices, Vec3d vec, Entity entity, float lerpHeightValue, Color color) {
      double lerp = MathHelper.lerp((double)lerpHeightValue, entity.lastX, entity.getX());
      double vertexBuilderValue = MathHelper.lerp((double)lerpHeightValue, entity.lastY, entity.getY()) + entity.getHeight() / 2.0;
      double lerp2 = MathHelper.lerp((double)lerpHeightValue, entity.lastZ, entity.getZ());
      float lerpHeightBound = (float)this.tracerAlpha.getValue();
      int rgb = new Color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, Math.min(1.0F, Math.max(0.0F, lerpHeightBound))).getRGB();
      int matrixValue = new Color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, Math.min(1.0F, Math.max(0.0F, lerpHeightBound * 0.4F))).getRGB();
      VectorDotUtils.handle(lerp, vertexBuilderValue, lerp2, rgb, matrixValue, (float)this.tracerWidth.getValue());
      VertexBuilderUtils.updateDrawImmediate();
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null) {
         Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
         float tickProgress = queueCommand.tickProgress;
         Color releaseRight = this.getEspColor();
         Color llamaEspColor = this.getLlamaEspColor();

         for (WanderingTraderEntity wanderingTraderEntity : this.traders) {
            if (wanderingTraderEntity.isAlive() && !wanderingTraderEntity.isRemoved()) {
               if (this.renderEsp.isEnabled()) {
                  this.renderEntityBox(queueCommand.matrices, cameraPos, wanderingTraderEntity, tickProgress, releaseRight);
               }

               if (this.tracers.isEnabled() && this.tracerTrader.isEnabled()) {
                  this.renderEntityTracer(queueCommand.matrices, cameraPos, wanderingTraderEntity, tickProgress, releaseRight);
               }
            }
         }

         for (TraderLlamaEntity traderLlamaEntity : this.nearbyLlamas) {
            if (traderLlamaEntity.isAlive() && !traderLlamaEntity.isRemoved()) {
               if (this.llamaEsp.isEnabled()) {
                  this.renderEntityBox(queueCommand.matrices, cameraPos, traderLlamaEntity, tickProgress, llamaEspColor);
               }

               if (this.tracers.isEnabled() && this.tracerLlamas.isEnabled()) {
                  Color color = new Color(llamaEspColor.getRed(), llamaEspColor.getGreen(), llamaEspColor.getBlue(), (int)(this.tracerAlpha.getValue() * 150.0));
                  this.renderEntityTracer(queueCommand.matrices, cameraPos, traderLlamaEntity, tickProgress, color);
               }
            }
         }
      }
   }

   public static enum HeatLevel {
      NONE,
      LOW,
      MEDIUM,
      HIGH;

      public Color getColor() {
         return switch (this) {
            case LOW -> new Color(255, 255, 0);
            case MEDIUM -> new Color(255, 165, 0);
            case HIGH -> new Color(255, 50, 50);
            default -> new Color(200, 200, 200);
         };
      }
   }
}
