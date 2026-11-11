package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorDotUtils;
import com.corz.client.render.VertexBuilderUtils;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class PlayerESP extends Module {
   private final BooleanSetting booleanBase;
   private final BooleanSetting skeletonJoints;
   private final IntegerSetting integerHelper;
   private final Map<Integer, PlayerESP.Swing> skeletonDrawsMap;
   private final IntegerSetting cornerAlpha;
   private final IntegerSetting lineWidth;
   private final EnumSetting enumHelper = new EnumSetting(
      "Mode", "Choose ESP mode", "Outline Glow", "Outline Glow", "Corner ESP", "Normal ESP", "Health Box", "Skeleton"
   );
   private final BooleanSetting tracers;
   private final BooleanSetting filledOutline;

   private void handleMultiplySubtract(MatrixStack matrices, PlayerEntity player, double lerp, double y, double lerp2, float delta, int multiplySubtractValue, int vertexBuilderValue, float z22, boolean multiplySubtractFlag) {
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      PlayerESP.Swing ifAbsent = this.skeletonDrawsMap.computeIfAbsent(player.getId(), pzx -> {
         PlayerESP.Swing swing = new PlayerESP.Swing();
         swing.lerpMultiplySubtract = lerp;
         swing.lerp2MultiplySubtract = lerp2;
         swing.nanoTimeMultiplySubtract = System.nanoTime();
         return swing;
      });
      ifAbsent.multiplySubtractActive = true;
      long nanoTime = System.nanoTime();
      double multiplySubtractDivisor = Math.min(0.1, Math.max(1.0E-4, (nanoTime - ifAbsent.nanoTimeMultiplySubtract) / 1.0E9));
      double multiplySubtractFactor = lerp - ifAbsent.lerpMultiplySubtract;
      double multiplySubtractFactor2 = lerp2 - ifAbsent.lerp2MultiplySubtract;
      double multiplySubtractFactor3 = Math.sqrt(multiplySubtractFactor * multiplySubtractFactor + multiplySubtractFactor2 * multiplySubtractFactor2) / multiplySubtractDivisor;
      ifAbsent.lerpMultiplySubtract = lerp;
      ifAbsent.lerp2MultiplySubtract = lerp2;
      ifAbsent.nanoTimeMultiplySubtract = nanoTime;
      double min4 = Math.min(1.0, multiplySubtractFactor3 / 4.3);
      ifAbsent.multiplySubtractAmount = ifAbsent.multiplySubtractAmount + (min4 - ifAbsent.multiplySubtractAmount) * Math.min(1.0, multiplySubtractDivisor * 12.0);
      ifAbsent.swingAngle += multiplySubtractFactor3 * multiplySubtractDivisor * 3.4;
      double angle = ifAbsent.swingAngle;
      double multiplySubtractAmount = ifAbsent.multiplySubtractAmount;
      float degrees = MathHelper.lerpAngleDegrees(delta, player.lastBodyYaw, player.bodyYaw);
      float lerpAngleDegrees = MathHelper.lerpAngleDegrees(delta, player.lastHeadYaw, player.headYaw);
      double radians = Math.toRadians(degrees);
      double radians3 = Math.toRadians(lerpAngleDegrees);
      double radians4 = Math.toRadians(MathHelper.lerp(delta, player.lastPitch, player.getPitch()));
      Vec3d vec3d = new Vec3d(-Math.sin(radians), 0.0, Math.cos(radians));
      Vec3d vec3d2 = new Vec3d(-Math.cos(radians), 0.0, -Math.sin(radians));
      Vec3d vec3d3 = new Vec3d(-Math.sin(radians3), 0.0, Math.cos(radians3));
      Vec3d vec3d4 = new Vec3d(lerp, y, lerp2);
      String name = player.getPose().name();
      boolean multiplySubtractFlag2 = name.equals("SWIMMING") || name.equals("GLIDING") || name.equals("FALL_FLYING");
      boolean multiplySubtractFlag3 = !multiplySubtractFlag2 && (name.equals("CROUCHING") || player.isSneaking());
      boolean vehicle = player.hasVehicle();
      Vec3d vec3d5;
      Vec3d vec3d6;
      Vec3d vec3d7;
      Vec3d vec3d8;
      Vec3d vec3d9;
      Vec3d vec3d10;
      Vec3d vec3d11;
      Vec3d vec3d12;
      Vec3d vec3d13;
      Vec3d vec3d14;
      Vec3d vec3d15;
      if (multiplySubtractFlag2) {
         vec3d5 = vec3d4.add(0.0, 0.35, 0.0);
         vec3d6 = vec3d5.add(vec3d.multiply(0.66));
         vec3d7 = vec3d6.add(vec3d.multiply(0.24)).add(0.0, 0.06, 0.0);
         vec3d8 = vec3d6.subtract(vec3d2.multiply(0.26));
         vec3d9 = vec3d6.add(vec3d2.multiply(0.26));
         vec3d10 = vec3d5.subtract(vec3d2.multiply(0.11));
         vec3d11 = vec3d5.add(vec3d2.multiply(0.11));
         double y2 = Math.sin(angle) * multiplySubtractAmount * 0.25;
         vec3d12 = vec3d8.subtract(vec3d2.multiply(0.18)).subtract(vec3d.multiply(0.45)).add(0.0, 0.05, 0.0);
         vec3d13 = vec3d9.add(vec3d2.multiply(0.18)).subtract(vec3d.multiply(0.45)).add(0.0, 0.05, 0.0);
         vec3d14 = vec3d10.subtract(vec3d.multiply(0.7)).add(0.0, y2, 0.0);
         vec3d15 = vec3d11.subtract(vec3d.multiply(0.7)).add(0.0, -y2, 0.0);
      } else {
         double y3 = multiplySubtractFlag3 ? 0.62 : 0.72;
         double multiplySubtractFactor4 = 0.66;
         double angle5 = multiplySubtractFlag3 ? 0.5 : 0.0;
         vec3d5 = vec3d4.add(0.0, y3, 0.0);
         vec3d6 = vec3d5.add(vec3d.multiply(multiplySubtractFactor4 * Math.sin(angle5))).add(0.0, multiplySubtractFactor4 * Math.cos(angle5), 0.0);
         vec3d7 = vec3d6.add(0.0, 0.22 * Math.cos(radians4), 0.0).add(vec3d3.multiply(0.22 * Math.sin(radians4)));
         vec3d8 = vec3d6.subtract(vec3d2.multiply(0.28));
         vec3d9 = vec3d6.add(vec3d2.multiply(0.28));
         vec3d10 = vec3d5.subtract(vec3d2.multiply(0.11));
         vec3d11 = vec3d5.add(vec3d2.multiply(0.11));
         double multiplySubtractValue5 = vehicle ? 1.35 : Math.sin(angle) * multiplySubtractAmount * 0.85;
         double multiplySubtractValue6 = Math.sin(angle) * multiplySubtractAmount * 0.6 + (multiplySubtractFlag3 ? 0.35 : 0.0);
         vec3d15 = getMultiply(vec3d11, vec3d, 0.72, multiplySubtractValue5);
         vec3d14 = getMultiply(vec3d10, vec3d, 0.72, vehicle ? 1.35 : -multiplySubtractValue5);
         vec3d13 = getMultiply(vec3d9, vec3d, 0.62, -multiplySubtractValue6);
         vec3d12 = getMultiply(vec3d8, vec3d, 0.62, multiplySubtractValue6);
      }

      VertexBuilderUtils.handle(positionMatrix, vec3d7, vec3d6, multiplySubtractValue, z22);
      VertexBuilderUtils.handle(positionMatrix, vec3d6, vec3d8, multiplySubtractValue, z22);
      VertexBuilderUtils.handle(positionMatrix, vec3d6, vec3d9, multiplySubtractValue, z22);
      VertexBuilderUtils.handle(positionMatrix, vec3d8, vec3d12, multiplySubtractValue, z22);
      VertexBuilderUtils.handle(positionMatrix, vec3d9, vec3d13, multiplySubtractValue, z22);
      VertexBuilderUtils.handle(positionMatrix, vec3d6, vec3d5, multiplySubtractValue, z22);
      VertexBuilderUtils.handle(positionMatrix, vec3d5, vec3d10, multiplySubtractValue, z22);
      VertexBuilderUtils.handle(positionMatrix, vec3d5, vec3d11, multiplySubtractValue, z22);
      VertexBuilderUtils.handle(positionMatrix, vec3d10, vec3d14, multiplySubtractValue, z22);
      VertexBuilderUtils.handle(positionMatrix, vec3d11, vec3d15, multiplySubtractValue, z22);
      if (multiplySubtractFlag) {
         float multiplySubtractValue7 = z22 * 2.5F + 3.0F;

         for (Vec3d vec3d16 : new Vec3d[]{vec3d7, vec3d12, vec3d13, vec3d5, vec3d14, vec3d15}) {
            VertexBuilderUtils.handle(positionMatrix, vec3d16, vec3d16.add(0.0, 0.003, 0.0), vertexBuilderValue, multiplySubtractValue7);
         }
      }
   }

   public boolean canGlowOutline(Entity entity) {
      return this.isEnabled()
         && this.enumHelper.getCurrentOption().equals("Outline Glow")
         && entity instanceof PlayerEntity
         && (entity != mc.player || ClampMatrixUtils.isInstanceScreen());
   }

   private static void handlePeekPosition(MatrixStack matrices, float peekPositionValue, float f2, float amountForDouble, float normalLineValue, float f5, float f6, Color color, int blockOverlayValue) {
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      VertexBuilderUtils.handle(positionMatrix, peekPositionValue, f2, amountForDouble, normalLineValue, f5, f6, color.getRGB(), blockOverlayValue);
   }

   public static Color getReleaseRight(int alpha2) {
      Color color = new Color(MatrixEspUtils.releaseRightValue);
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha2);
   }

   private static void handlePeekPositionForMatrixStackFloat(MatrixStack matrices, float peekPositionValue, float deltaY, float amountForDouble, float vertexBuilderValue, float y, float peekPositionValue6, Color color, int peekPositionValue7) {
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      int rgb = color.getRGB();
      float z22 = peekPositionValue7;
      float peekPositionValue8 = (vertexBuilderValue - peekPositionValue) * 0.25F;
      float peekPositionValue9 = (y - deltaY) * 0.25F;
      float peekPositionValue10 = (peekPositionValue6 - amountForDouble) * 0.25F;
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, deltaY, amountForDouble, peekPositionValue + peekPositionValue8, deltaY, amountForDouble, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, deltaY, amountForDouble, peekPositionValue, deltaY + peekPositionValue9, amountForDouble, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, deltaY, amountForDouble, peekPositionValue, deltaY, amountForDouble + peekPositionValue10, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, deltaY, amountForDouble, vertexBuilderValue - peekPositionValue8, deltaY, amountForDouble, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, deltaY, amountForDouble, vertexBuilderValue, deltaY + peekPositionValue9, amountForDouble, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, deltaY, amountForDouble, vertexBuilderValue, deltaY, amountForDouble + peekPositionValue10, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, deltaY, peekPositionValue6, vertexBuilderValue - peekPositionValue8, deltaY, peekPositionValue6, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, deltaY, peekPositionValue6, vertexBuilderValue, deltaY + peekPositionValue9, peekPositionValue6, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, deltaY, peekPositionValue6, vertexBuilderValue, deltaY, peekPositionValue6 - peekPositionValue10, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, deltaY, peekPositionValue6, peekPositionValue + peekPositionValue8, deltaY, peekPositionValue6, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, deltaY, peekPositionValue6, peekPositionValue, deltaY + peekPositionValue9, peekPositionValue6, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, deltaY, peekPositionValue6, peekPositionValue, deltaY, peekPositionValue6 - peekPositionValue10, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, y, amountForDouble, peekPositionValue + peekPositionValue8, y, amountForDouble, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, y, amountForDouble, peekPositionValue, y - peekPositionValue9, amountForDouble, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, y, amountForDouble, peekPositionValue, y, amountForDouble + peekPositionValue10, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, y, amountForDouble, vertexBuilderValue - peekPositionValue8, y, amountForDouble, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, y, amountForDouble, vertexBuilderValue, y - peekPositionValue9, amountForDouble, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, y, amountForDouble, vertexBuilderValue, y, amountForDouble + peekPositionValue10, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, y, peekPositionValue6, vertexBuilderValue - peekPositionValue8, y, peekPositionValue6, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, y, peekPositionValue6, vertexBuilderValue, y - peekPositionValue9, peekPositionValue6, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, y, peekPositionValue6, vertexBuilderValue, y, peekPositionValue6 - peekPositionValue10, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, y, peekPositionValue6, peekPositionValue + peekPositionValue8, y, peekPositionValue6, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, y, peekPositionValue6, peekPositionValue, y - peekPositionValue9, peekPositionValue6, rgb, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, peekPositionValue, y, peekPositionValue6, peekPositionValue, y, peekPositionValue6 - peekPositionValue10, rgb, z22);
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null) {
         String formatText = this.enumHelper.getCurrentOption();
         Color releaseRight = getReleaseRight(this.cornerAlpha.getValue());
         Color releaseRight2 = getReleaseRight(this.integerHelper.getValue());
         Color releaseRight3 = getReleaseRight(255);
         Color color = new Color(255, 255, 255, 235);

         for (PlayerEntity playerEntity : mc.world.getPlayers()) {
            if ((playerEntity != mc.player || ClampMatrixUtils.isInstanceScreen()) && !FriendNameStore.canFlag(playerEntity)) {
               Camera camera = mc.gameRenderer.getCamera();
               queueCommand.matrices.push();
               if (camera != null) {
                  Vec3d cameraPos = camera.getCameraPos();
                  queueCommand.matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
               }

               float tickProgress = queueCommand.tickProgress;
               double lerp = MathHelper.lerp((double)tickProgress, playerEntity.lastX, playerEntity.getX());
               double lerp2 = MathHelper.lerp((double)tickProgress, playerEntity.lastY, playerEntity.getY());
               double lerp3 = MathHelper.lerp((double)tickProgress, playerEntity.lastZ, playerEntity.getZ());
               float widthScaled = playerEntity.getWidth() / 2.0F;
               float height = playerEntity.getHeight();
               float playerEspValue = (float)lerp - widthScaled;
               float deltaY = (float)lerp2;
               float playerEspValue3 = (float)lerp3 - widthScaled;
               float vertexBuilderValue = (float)lerp + widthScaled;
               float y = (float)lerp2 + height;
               float amountForDouble = (float)lerp3 + widthScaled;
               if (formatText.equals("Corner ESP")) {
                  handlePeekPositionForMatrixStackFloat(queueCommand.matrices, playerEspValue, deltaY, playerEspValue3, vertexBuilderValue, y, amountForDouble, releaseRight, this.lineWidth.getValue());
               } else if (formatText.equals("Normal ESP")) {
                  handlePeekPosition(queueCommand.matrices, playerEspValue, deltaY, playerEspValue3, vertexBuilderValue, y, amountForDouble, releaseRight2);
                  if (this.filledOutline.isEnabled()) {
                     handlePeekPosition(queueCommand.matrices, playerEspValue, deltaY, playerEspValue3, vertexBuilderValue, y, amountForDouble, releaseRight3, this.lineWidth.getValue());
                  }
               } else if (formatText.equals("Health Box")) {
                  handlePeekPosition(queueCommand.matrices, playerEspValue, deltaY, playerEspValue3, vertexBuilderValue, y, amountForDouble, color, this.lineWidth.getValue());
                  if (camera != null) {
                     handleHealthCos(queueCommand.matrices, playerEntity, lerp, lerp2, lerp3, widthScaled, height, camera, this.lineWidth.getValue());
                  }
               } else if (formatText.equals("Skeleton")) {
                  if (this.booleanBase.isEnabled()) {
                     handlePeekPosition(queueCommand.matrices, playerEspValue, deltaY, playerEspValue3, vertexBuilderValue, y, amountForDouble, getReleaseRight(170), this.lineWidth.getValue());
                  }

                  this.handleMultiplySubtract(
                     queueCommand.matrices,
                     playerEntity,
                     lerp,
                     lerp2,
                     lerp3,
                     tickProgress,
                     -419430401,
                     releaseRight3.getRGB(),
                     this.lineWidth.getValue(),
                     this.skeletonJoints.isEnabled()
                  );
               }

               if (this.tracers.isEnabled() && camera != null) {
                  VectorDotUtils.handle(lerp, lerp2 + playerEntity.getHeight() * 0.5, lerp3, releaseRight3.getRGB(), 1.0F);
               }

               queueCommand.matrices.pop();
            }
         }

         this.skeletonDrawsMap.values().removeIf(swing -> !swing.multiplySubtractActive);

         for (PlayerESP.Swing swing2 : this.skeletonDrawsMap.values()) {
            swing2.multiplySubtractActive = false;
         }

         VertexBuilderUtils.updateDrawImmediate();
      }
   }

   private static Vec3d getMultiply(Vec3d vec, Vec3d vec2, double multiplyFactor, double angle) {
      return vec.add(vec2.multiply(multiplyFactor * Math.sin(angle))).add(0.0, -multiplyFactor * Math.cos(angle), 0.0);
   }

   public int getReleaseRight() {
      Color color = new Color(MatrixEspUtils.releaseRightValue);
      return 0xFF000000 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
   }

   private static void handlePeekPosition(MatrixStack matrices, float peekPositionValue, float deltaY, float peekPositionValue3, float vertexColorValue, float y, float amountForDouble, Color color) {
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      VertexBuilderUtils.handle(
         positionMatrix,
         peekPositionValue,
         deltaY,
         peekPositionValue3,
         vertexColorValue,
         y,
         amountForDouble,
         color.getRed() / 255.0F,
         color.getGreen() / 255.0F,
         color.getBlue() / 255.0F,
         color.getAlpha() / 255.0F
      );
   }

   private static void handleHealthCos(
      MatrixStack matrices, PlayerEntity playerEntity, double healthCosValue, double healthCosValue2, double healthCosValue3, float widthScaled, float healthCosFactor, Camera camera, int healthCosValue5
   ) {
      float maxMaxHealth = Math.max(1.0F, playerEntity.getMaxHealth());
      float clamp = MathHelper.clamp(playerEntity.getHealth() / maxMaxHealth, 0.0F, 1.0F);
      float angle = camera.getYaw() * (float) (Math.PI / 180.0);
      float lineWidthOffset = widthScaled + 0.18F;
      float vertexBuilderValue = (float)healthCosValue + MathHelper.cos(angle) * lineWidthOffset;
      float amountForDouble = (float)healthCosValue3 + MathHelper.sin(angle) * lineWidthOffset;
      float deltaY = (float)healthCosValue2;
      float y = deltaY + healthCosFactor;
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      float max9 = Math.max(3.0F, healthCosValue5 + 2.0F);
      float z22 = Math.max(1.5F, healthCosValue5 + 0.5F);
      VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, deltaY, amountForDouble, vertexBuilderValue, y, amountForDouble, -939524096, max9);
      if (clamp > 0.001F) {
         int playerValue = 0xFF000000 | Color.HSBtoRGB(clamp * 0.33F, 0.88F, 1.0F) & 16777215;
         VertexBuilderUtils.handleForMatrix4fFloat(positionMatrix, vertexBuilderValue, deltaY, amountForDouble, vertexBuilderValue, deltaY + healthCosFactor * clamp, amountForDouble, playerValue, z22);
      }
   }

   public PlayerESP() {
      super("PlayerESP", IconsPngType.VISUAL);
      this.integerHelper = new IntegerSetting("Alpha", "Filled box alpha", 80, 1, 255);
      this.cornerAlpha = new IntegerSetting("Corner Alpha", "Corner ESP alpha", 255, 1, 255);
      this.lineWidth = new IntegerSetting("Line Width", "", 1, 1, 10);
      this.filledOutline = new BooleanSetting("Filled Outline", "Outline around filled box", false);
      this.booleanBase = new BooleanSetting("Skeleton Box", "Skeleton mode: also draw the outline box", true);
      this.skeletonJoints = new BooleanSetting("Skeleton Joints", "Skeleton mode: dots on head, hands, hips and feet", true);
      this.tracers = new BooleanSetting("Tracers", "Draws a line from your player to the other", false);
      this.skeletonDrawsMap = new HashMap<>();
      this.addSettings(
         new ConfigSetting[]{
            this.enumHelper,
            this.integerHelper,
            this.cornerAlpha,
            this.lineWidth,
            this.filledOutline,
            this.booleanBase,
            this.skeletonJoints,
            this.tracers
         }
      );
   }

   private static final class Swing {
      long nanoTimeMultiplySubtract;
      double swingAngle;
      double lerp2MultiplySubtract;
      double multiplySubtractAmount;
      double lerpMultiplySubtract;
      boolean multiplySubtractActive;
   }
}
