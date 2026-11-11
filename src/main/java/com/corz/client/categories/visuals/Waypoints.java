package com.corz.client.categories.visuals;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.KeybindSetting;
import com.corz.client.config.StringBase;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.TickProgress;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.TexBuilderUtils;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import com.corz.client.render.VertexTextureFont;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.FovOptionsUtils;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.SwordBot;

public class Waypoints extends Module {
   private final KeybindSetting keybindBase;
   private final StringBase stringBase = new StringBase("Name", "Name for the next waypoint you add", "Waypoint");
   private final ButtonBase buttonBase;
   private final BooleanSetting booleanBase;
   private final BooleanSetting waypointsBooleanBase;
   private static final Identifier identifier = Identifier.of("corzclient", "textures/world/wp/waypoint.png");
   private final BooleanSetting labelSetting;
   private final List<Waypoints.Waypoint> waypointsClearedItems;
   private static final Color OFF_WHITE = new Color(240, 243, 248, 255);
   private final DoubleSetting doubleHelper;
   private final DoubleSetting beamHeight;
   private final BooleanSetting edgeIndicator;
   private Immediate immediate;
   private BufferAllocator bufferAllocator;
   private static final Color DISTANCE_LABEL_COLOR = new Color(160, 166, 178, 235);
   private final ButtonBase waypointsButtonBase;
   private final ButtonBase addHere = new ButtonBase("Add Here", "Drop a waypoint at your current position", "Add", this::updateWaypointsClearedWaypointsClearedItems);

   @Override
   protected void onSwordBot(SwordBot swordBot) {
      if (this.isEnabled() && swordBot.getValue() == 1) {
         if (mc.player != null && mc.currentScreen == null) {
            if (this.keybindBase.isBoundTo(swordBot.getKey())) {
               this.updateWaypointsClearedWaypointsClearedItems();
            }
         }
      }
   }

   @Override
   public void loadExtraData(JsonElement json) {
      if (json != null && json.isJsonObject()) {
         JsonObject asJsonObject = json.getAsJsonObject();
         if (asJsonObject.has("waypoints") && asJsonObject.get("waypoints").isJsonArray()) {
            this.waypointsClearedItems.clear();

            for (JsonElement jsonElement : asJsonObject.getAsJsonArray("waypoints")) {
               if (jsonElement.isJsonObject()) {
                  JsonObject asJsonObject2 = jsonElement.getAsJsonObject();

                  try {
                     this.waypointsClearedItems
                        .add(
                           new Waypoints.Waypoint(
                              asJsonObject2.get("x").getAsDouble(),
                              asJsonObject2.get("y").getAsDouble(),
                              asJsonObject2.get("z").getAsDouble(),
                              asJsonObject2.has("name") ? asJsonObject2.get("name").getAsString() : "wp",
                              asJsonObject2.has("color") ? asJsonObject2.get("color").getAsInt() & 16777215 : '\uaaff'
                           )
                        );
                     if (this.waypointsClearedItems.size() >= 64) {
                        break;
                     }
                  } catch (Exception e) {
                  }
               }
            }
         }
      }
   }

   @Override
   public void onDisable() {
      if (this.bufferAllocator != null) {
         this.bufferAllocator.close();
         this.bufferAllocator = null;
         this.immediate = null;
      }
   }

   private void updateWaypointsCleared() {
      if (!this.waypointsClearedItems.isEmpty()) {
         int waypointsClearedValue = this.waypointsClearedItems.size();
         this.waypointsClearedItems.clear();
         DisabledEnabled.getInstance().handleDisabledEnabled("Waypoints Cleared", waypointsClearedValue + " removed", false);
         ModuleConfigAutosave.updateFlag2();
      }
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      if (this.isEnabled() && mc.player != null && mc.world != null && !this.waypointsClearedItems.isEmpty()) {
         Camera camera = mc.gameRenderer.getCamera();
         Vec3d cameraPos = camera.getCameraPos();
         long currentMillis = System.currentTimeMillis();
         float waypointsFactor = 0.85F + 0.15F * (float)Math.sin((float)(currentMillis % 10000L) / 1000.0F * 3.0F);
         Matrix4f positionMatrix = queueCommand.matrices.peek().getPositionMatrix();
         if (this.waypointsBooleanBase.isEnabled()) {
            float waypointsValue = (float)this.beamHeight.getValue();
            VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();

            for (Waypoints.Waypoint waypoint : this.waypointsClearedItems) {
               float deltaX = (float)(waypoint.x - cameraPos.x);
               float deltaZ = (float)(waypoint.z - cameraPos.z);
               float deltaY = (float)(waypoint.y - cameraPos.y);
               double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
               float waypointsFactor2 = distance > 0.001 ? (float)(deltaZ / distance) : 1.0F;
               float waypointsFactor3 = distance > 0.001 ? (float)(-deltaX / distance) : 0.0F;
               float waypointsFactor4 = 0.12F;
               int valueInt = MatrixAmountUtils.getValueInt(waypoint.color, 0.55F * waypointsFactor);
               int valueInt3 = MatrixAmountUtils.getValueInt(waypoint.color, 0.0F);
               VertexBuilderUtils.handleVertexColor(
                  buffer,
                  positionMatrix,
                  deltaX + waypointsFactor2 * waypointsFactor4,
                  deltaY,
                  deltaZ + waypointsFactor3 * waypointsFactor4,
                  valueInt,
                  deltaX - waypointsFactor2 * waypointsFactor4,
                  deltaY,
                  deltaZ - waypointsFactor3 * waypointsFactor4,
                  valueInt,
                  deltaX - waypointsFactor2 * waypointsFactor4,
                  deltaY + waypointsValue,
                  deltaZ - waypointsFactor3 * waypointsFactor4,
                  valueInt3,
                  deltaX + waypointsFactor2 * waypointsFactor4,
                  deltaY + waypointsValue,
                  deltaZ + waypointsFactor3 * waypointsFactor4,
                  valueInt3
               );
            }
         }

         if (this.booleanBase.isEnabled()) {
            if (this.bufferAllocator == null) {
               this.bufferAllocator = new BufferAllocator(524288);
               this.immediate = VertexConsumerProvider.immediate(this.bufferAllocator);
            }

            RenderLayer apply = TexBuilderUtils.texBuilderFunction.apply(identifier);
            VertexConsumer buffer2 = this.immediate.getBuffer(apply);
            float x = (float)this.doubleHelper.getValue() * waypointsFactor;
            MatrixStack matrices = queueCommand.matrices;

            for (Waypoints.Waypoint waypoint2 : this.waypointsClearedItems) {
               int waypointsValue4 = waypoint2.color >> 16 & 0xFF;
               int waypointsValue5 = waypoint2.color >> 8 & 0xFF;
               int waypointsValue6 = waypoint2.color & 0xFF;
               int waypointsValue7 = (int)(255.0F * waypointsFactor);
               matrices.push();
               matrices.translate(waypoint2.x - cameraPos.x, waypoint2.y - cameraPos.y + 1.2, waypoint2.z - cameraPos.z);
               matrices.multiply(camera.getRotation());
               matrices.scale(x, x, 1.0F);
               Matrix4f positionMatrix2 = matrices.peek().getPositionMatrix();
               buffer2.vertex(positionMatrix2, -1.0F, -1.0F, 0.0F).texture(0.0F, 1.0F).color(waypointsValue4, waypointsValue5, waypointsValue6, waypointsValue7);
               buffer2.vertex(positionMatrix2, 1.0F, -1.0F, 0.0F).texture(1.0F, 1.0F).color(waypointsValue4, waypointsValue5, waypointsValue6, waypointsValue7);
               buffer2.vertex(positionMatrix2, 1.0F, 1.0F, 0.0F).texture(1.0F, 0.0F).color(waypointsValue4, waypointsValue5, waypointsValue6, waypointsValue7);
               buffer2.vertex(positionMatrix2, -1.0F, 1.0F, 0.0F).texture(0.0F, 0.0F).color(waypointsValue4, waypointsValue5, waypointsValue6, waypointsValue7);
               matrices.pop();
            }

            this.immediate.draw();
         }
      }
   }

   private void updateWaypointsClearedWaypointsClearedItems() {
      if (mc.player != null) {
         if (this.waypointsClearedItems.size() >= 64) {
            DisabledEnabled.getInstance().handleDisabledEnabled("Waypoints", "Limit of 64 reached", false);
         } else {
            Vec3d entityPos = mc.player.getEntityPos();
            String waypointsClearedText = this.stringBase.getCurrentText() != null && !this.stringBase.getCurrentText().isBlank()
               ? this.stringBase.getCurrentText().trim()
               : "wp" + (this.waypointsClearedItems.size() + 1);
            int waypointsClearedValue = Color.HSBtoRGB(this.waypointsClearedItems.size() * 0.37F % 1.0F, 0.85F, 1.0F) & 16777215;
            this.waypointsClearedItems.add(new Waypoints.Waypoint(entityPos.x, entityPos.y, entityPos.z, waypointsClearedText, waypointsClearedValue));
            DisabledEnabled.getInstance().handleDisabledEnabled("Waypoint Added", String.format("%s  %.0f %.0f %.0f", waypointsClearedText, entityPos.x, entityPos.y, entityPos.z), true);
            ModuleConfigAutosave.updateFlag2();
         }
      }
   }

   private void updateWaypointsCleared3() {
      if (mc.player != null && !this.waypointsClearedItems.isEmpty()) {
         Vec3d entityPos = mc.player.getEntityPos();
         int waypointsClearedLimit = -1;
         double maxValue = Double.MAX_VALUE;

         for (int i = 0; i < this.waypointsClearedItems.size(); i++) {
            double squaredDistanceTo = entityPos.squaredDistanceTo(this.waypointsClearedItems.get(i).getVec3d());
            if (squaredDistanceTo < maxValue) {
               maxValue = squaredDistanceTo;
               waypointsClearedLimit = i;
            }
         }

         if (waypointsClearedLimit >= 0) {
            Waypoints.Waypoint waypointscleareditem = this.waypointsClearedItems.remove(waypointsClearedLimit);
            DisabledEnabled.getInstance().handleDisabledEnabled("Waypoint Removed", waypointscleareditem.name, false);
            ModuleConfigAutosave.updateFlag2();
         }
      }
   }

   private static float getAmount(float waypointsBound, float waypointsLimit, float hashHuntBound) {
      return waypointsBound < waypointsLimit ? waypointsLimit : Math.min(waypointsBound, hashHuntBound);
   }

   public Waypoints() {
      super("Waypoints", IconsPngType.VISUAL);
      this.keybindBase = new KeybindSetting("Set Waypoint Key", "Press this key to drop a waypoint at your current position");
      this.waypointsButtonBase = new ButtonBase("Remove Nearest", "Delete the waypoint closest to you", "Remove", this::updateWaypointsCleared3).getFlagButtonActive();
      this.buttonBase = new ButtonBase("Clear All", "Delete every waypoint", "Clear", this::updateWaypointsCleared).getFlagButtonActive();
      this.waypointsBooleanBase = new BooleanSetting("Beam", "Draw a vertical beacon beam", true);
      this.booleanBase = new BooleanSetting("Icon", "Draw the floating diamond marker", true);
      this.labelSetting = new BooleanSetting("Label", "Draw the name + distance label", true);
      this.edgeIndicator = new BooleanSetting("Edge Indicator", "Pin the label to the screen edge when off-screen", true);
      this.doubleHelper = new DoubleSetting("Icon Size", "Size of the diamond marker", 0.9, 0.3, 3.0, 0.1);
      this.beamHeight = new DoubleSetting("Beam Height", "Height of the beacon beam (blocks)", 40.0, 4.0, 128.0, 1.0);
      this.waypointsClearedItems = new ArrayList<>();
      this.addSettings(
         new ConfigSetting[]{
            this.stringBase,
            this.addHere,
            this.keybindBase,
            this.waypointsButtonBase,
            this.buttonBase,
            this.waypointsBooleanBase,
            this.booleanBase,
            this.labelSetting,
            this.edgeIndicator,
            this.doubleHelper,
            this.beamHeight
         }
      );
   }

   @Override
   public JsonElement saveExtraData() {
      JsonObject jsonObject = new JsonObject();
      JsonArray jsonArray = new JsonArray();

      for (Waypoints.Waypoint waypoint : this.waypointsClearedItems) {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("x", waypoint.x);
         jsonObject2.addProperty("y", waypoint.y);
         jsonObject2.addProperty("z", waypoint.z);
         jsonObject2.addProperty("name", waypoint.name);
         jsonObject2.addProperty("color", waypoint.color);
         jsonArray.add(jsonObject2);
      }

      jsonObject.add("waypoints", jsonArray);
      return jsonObject;
   }

   @Override
   public void onRender2D(TickProgress tickProgress) {
      if (this.isEnabled() && mc.player != null && mc.world != null && !this.waypointsClearedItems.isEmpty()) {
         if (this.labelSetting.isEnabled() && mc.currentScreen == null) {
            Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
            int scaledWidth = mc.getWindow().getScaledWidth();
            int scaledHeight = mc.getWindow().getScaledHeight();
            float radians = (float)Math.toRadians(mc.gameRenderer.getCamera().getYaw());
            float radians2 = (float)Math.toRadians(mc.gameRenderer.getCamera().getPitch());
            float radians3 = (float)Math.toRadians(FovOptionsUtils.getValue());
            float waypointsValue = radians3 * ((float)scaledWidth / scaledHeight);
            float waypointsValue2 = 18.0F;
            float waypointsFactor = scaledWidth / 2.0F - waypointsValue2;
            float waypointsFactor2 = scaledHeight / 2.0F - waypointsValue2;
            MatrixStack matrixStack = new MatrixStack();
            CorzClient.fontManager.setMatrixStack(matrixStack);
            MatrixPeekUtilsRender.handleMatrixStack(matrixStack);
            VertexTextureFont vertexTexture = CorzClient.fontManager.getLargeFont();
            int round = Math.round(vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown());

            for (Waypoints.Waypoint waypoint : this.waypointsClearedItems) {
               double distanceTo = cameraPos.distanceTo(waypoint.getVec3d());
               Vec3d vec3d = new Vec3d(waypoint.x, waypoint.y + 1.2, waypoint.z);
               Vec3d windowFramebuffer = VertexConsumerUtilsVector.getWindowFramebuffer(vec3d);
               boolean waypointsFlag = windowFramebuffer != null && windowFramebuffer.z > 0.0 && windowFramebuffer.z < 1.0;
               float waypointsValue4;
               float waypointsValue5;
               if (waypointsFlag && windowFramebuffer.x >= waypointsValue2 && windowFramebuffer.x <= scaledWidth - waypointsValue2 && windowFramebuffer.y >= waypointsValue2 && windowFramebuffer.y <= scaledHeight - waypointsValue2) {
                  waypointsValue4 = (float)windowFramebuffer.x;
                  waypointsValue5 = (float)windowFramebuffer.y;
               } else {
                  if (!this.edgeIndicator.isEnabled()) {
                     continue;
                  }

                  double spotValue;
                  double waypointsValue7;
                  if (waypointsFlag) {
                     spotValue = windowFramebuffer.x - scaledWidth / 2.0;
                     waypointsValue7 = windowFramebuffer.y - scaledHeight / 2.0;
                  } else {
                     double deltaX = vec3d.x - cameraPos.x;
                     double deltaY = vec3d.y - cameraPos.y;
                     double deltaZ = vec3d.z - cameraPos.z;
                     double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                     double waypointsLimit = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(-deltaX, deltaZ)) - Math.toDegrees(radians)) * Math.PI / 180.0;
                     double waypointsValue8 = -Math.atan2(deltaY, distance) - radians2;
                     boolean waypointsFlag2 = Math.abs(waypointsLimit) < Math.PI / 2;
                     spotValue = (waypointsFlag2 ? waypointsLimit / (waypointsValue / 2.0) : (waypointsLimit >= 0.0 ? 1.0 : -1.0)) * waypointsFactor;
                     waypointsValue7 = waypointsValue8 / (radians3 / 2.0) * waypointsFactor2;
                  }

                  double waypointsDivisor = Math.max(Math.abs(spotValue) / waypointsFactor, Math.abs(waypointsValue7) / waypointsFactor2);
                  if (waypointsDivisor < 0.001) {
                     continue;
                  }

                  waypointsValue4 = (float)(scaledWidth / 2.0F + spotValue / waypointsDivisor);
                  waypointsValue5 = (float)(scaledHeight / 2.0F + waypointsValue7 / waypointsDivisor);
               }

               String waypointsText = (int)distanceTo + "m";
               byte waypointsValue9 = 7;
               byte releaseRightLimit = 5;
               byte waypointsValue11 = 5;
               byte waypointsValue12 = 5;
               int round13 = Math.round(vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(waypoint.name));
               int round14 = Math.round(vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(waypointsText));
               int waypointsValue15 = waypointsValue9 + releaseRightLimit + waypointsValue11 + round13 + waypointsValue12 + round14 + waypointsValue9;
               int n4 = round + 8;
               int iconSize = n4 / 2;
               int matrixStackValue = Math.round(getAmount(waypointsValue4 - waypointsValue15 / 2.0F, 2.0F, scaledWidth - waypointsValue15 - 2.0F));
               int editValue = Math.round(getAmount(waypointsValue5 - n4 / 2.0F, 2.0F, scaledHeight - n4 - 2.0F));
               MatrixPeekUtilsRender.renderMatrixStack(tickProgress.drawContext, matrixStackValue - 1, editValue - 1, waypointsValue15 + 2, n4 + 2, iconSize + 1, 620756991);
               MatrixPeekUtilsRender.renderMatrixStack(tickProgress.drawContext, matrixStackValue, editValue, waypointsValue15, n4, iconSize, -670364398);
               MatrixPeekUtilsRender.renderMatrixStack(
                  tickProgress.drawContext, matrixStackValue + waypointsValue9, editValue + (n4 - releaseRightLimit) / 2, releaseRightLimit, releaseRightLimit, 2, 0xFF000000 | waypoint.color & 16777215
               );
               int vertexTextureValue = matrixStackValue + waypointsValue9 + releaseRightLimit + waypointsValue11;
               int abcdefghijklmnopqrstuvwxyzBrown = vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown(editValue, n4);
               vertexTexture.handleForStringFloat(waypoint.name, vertexTextureValue, abcdefghijklmnopqrstuvwxyzBrown, OFF_WHITE);
               vertexTexture.handleForStringFloat(waypointsText, vertexTextureValue + round13 + waypointsValue12, abcdefghijklmnopqrstuvwxyzBrown, DISTANCE_LABEL_COLOR);
            }
         }
      }
   }

   public record Waypoint(double x, double y, double z, String name, int color) {
      Vec3d getVec3d() {
         return new Vec3d(this.x, this.y, this.z);
      }
   }
}
