package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.TexBuilderUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class FireFly extends Module {
   private final IntegerSetting integerHelper = new IntegerSetting("Count", "Number of fireflies", 20, 5, 120);
   private BufferAllocator bufferAllocator;
   private final BooleanSetting booleanBase = new BooleanSetting("Theme Color", "Tint fireflies with the client theme instead of a rainbow palette", true);
   private Immediate immediate;
   private final Random random;
   private final List<FireFly.FireFlyEntity> firefliesThemeItems = new ArrayList<>();

   private void handleRandomFireflies(Vec3d vec) {
      double randomFirefliesFactor = this.random.nextDouble() * 30.0 + 5.0;
      double angle = Math.toRadians(this.random.nextDouble() * 360.0);
      double radians = Math.toRadians((this.random.nextDouble() - 0.5) * 60.0);
      double randomFirefliesValue = -Math.sin(angle) * Math.cos(radians) * 0.35F;
      double randomFirefliesValue2 = Math.sin(radians) * 0.35F * 0.5;
      double randomFirefliesValue3 = Math.cos(angle) * Math.cos(radians) * 0.35F;
      int[] ints = new int[]{16766720, 16776960, 65280, 65535, 16738740, 16753920, 49151};
      int randomFirefliesValue4 = 0xFF000000 | ints[this.random.nextInt(ints.length)];
      this.firefliesThemeItems
         .add(
            new FireFly.FireFlyEntity(
               vec.x - Math.sin(angle) * randomFirefliesFactor,
               vec.y + (this.random.nextDouble() - 0.3) * 8.0 + 1.0,
               vec.z + Math.cos(angle) * randomFirefliesFactor,
               randomFirefliesValue,
               randomFirefliesValue2,
               randomFirefliesValue3,
               randomFirefliesValue4,
               25
            )
         );
   }

   private static void handleVertexTexture(VertexConsumer vertices, Matrix4f matrix, float vertexTextureValue, int vertexTextureValue2) {
      float argbScaled = vertexTextureValue * 0.5F;
      int vertexTextureValue3 = vertexTextureValue2 >> 16 & 0xFF;
      int vertexTextureValue4 = vertexTextureValue2 >> 8 & 0xFF;
      int vertexTextureValue5 = vertexTextureValue2 & 0xFF;
      int vertexTextureValue6 = vertexTextureValue2 >>> 24;
      vertices.vertex(matrix, -argbScaled, -argbScaled, 0.0F).texture(0.0F, 1.0F).color(vertexTextureValue3, vertexTextureValue4, vertexTextureValue5, vertexTextureValue6);
      vertices.vertex(matrix, argbScaled, -argbScaled, 0.0F).texture(1.0F, 1.0F).color(vertexTextureValue3, vertexTextureValue4, vertexTextureValue5, vertexTextureValue6);
      vertices.vertex(matrix, argbScaled, argbScaled, 0.0F).texture(1.0F, 0.0F).color(vertexTextureValue3, vertexTextureValue4, vertexTextureValue5, vertexTextureValue6);
      vertices.vertex(matrix, -argbScaled, argbScaled, 0.0F).texture(0.0F, 0.0F).color(vertexTextureValue3, vertexTextureValue4, vertexTextureValue5, vertexTextureValue6);
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      if (this.isEnabled() && mc.player != null && mc.world != null && !this.firefliesThemeItems.isEmpty()) {
         if (this.bufferAllocator == null) {
            this.bufferAllocator = new BufferAllocator(1048576);
            this.immediate = VertexConsumerProvider.immediate(this.bufferAllocator);
         }

         MatrixStack matrices = queueCommand.matrices;
         Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
         float tickProgress = mc.getRenderTickCounter().getTickProgress(true);
         boolean booleanValue = this.booleanBase.isEnabled();
         RenderLayer apply = TexBuilderUtils.memoize.apply(TexBuilderUtils.identifier);
         VertexConsumer buffer = this.immediate.getBuffer(apply);
         matrices.push();
         matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
         Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

         for (int i = 0; i < this.firefliesThemeItems.size(); i++) {
            FireFly.FireFlyEntity firefliesthemeitem = this.firefliesThemeItems.get(i);
            float fireFlyFactor = firefliesthemeitem.getTime();
            int fireFlyLimit = Math.min(firefliesthemeitem.clampTrailPointList.size(), 10);
            if (!(fireFlyFactor <= 0.01F) && fireFlyLimit >= 2) {
               int fireFlyValue = booleanValue ? this.getReleaseRight(i) : firefliesthemeitem.colorArgb;
               double lerp = firefliesthemeitem.getLerp(tickProgress);
               double lerpForFloat = firefliesthemeitem.getLerpForFloat(tickProgress);
               double lerpStartLerp = firefliesthemeitem.getLerpStartLerp(tickProgress);
               Vec3d vec3d = new Vec3d(lerp, lerpForFloat, lerpStartLerp);
               var arrayList = new ArrayList();
               arrayList.add(vec3d);
               arrayList.add(new Vec3d(firefliesthemeitem.lerpAmount, firefliesthemeitem.startClamp, firefliesthemeitem.startLerp));

               for (int j = 1; j < fireFlyLimit; j++) {
                  FireFly.TrailPoint trailPoint = firefliesthemeitem.clampTrailPointList.get(j);
                  arrayList.add(new Vec3d(trailPoint.trailPointAmount, trailPoint.posY, trailPoint.posZ));
               }

               for (int k = 0; k < arrayList.size() - 1; k++) {
                  Vec3d vec3d2 = (Vec3d)arrayList.get(k);
                  Vec3d vec3d3 = (Vec3d)arrayList.get(k + 1);
                  float fireFlyValue2 = (float)k / (arrayList.size() - 1);
                  float fireFlyValue3 = (float)(k + 1) / (arrayList.size() - 1);
                  float fireFlyFactor2 = 0.05F * (1.0F - fireFlyValue2);
                  float fireFlyFactor3 = 0.05F * (1.0F - fireFlyValue3);
                  float matrixLimit = k == 0 ? 0.0F : (float)Math.pow(1.0F - fireFlyValue2, 2.0) * fireFlyFactor;
                  float fireFlyValue5 = (float)Math.pow(1.0F - fireFlyValue3, 2.0) * fireFlyFactor;
                  Vec3d subtract = vec3d3.subtract(vec3d2);
                  if (!(subtract.lengthSquared() < 1.0E-4)) {
                     Vec3d normalize = subtract.normalize();
                     Vec3d normalize2 = vec3d2.subtract(cameraPos).normalize();
                     Vec3d normalize3 = normalize.crossProduct(normalize2).normalize();
                     int valueInt = MatrixAmountUtils.getValueInt(fireFlyValue, matrixLimit);
                     int valueInt7 = MatrixAmountUtils.getValueInt(fireFlyValue, fireFlyValue5);
                     buffer.vertex(positionMatrix, (float)(vec3d2.x + normalize3.x * fireFlyFactor2), (float)(vec3d2.y + normalize3.y * fireFlyFactor2), (float)(vec3d2.z + normalize3.z * fireFlyFactor2))
                        .texture(0.5F, 0.0F)
                        .color(valueInt);
                     buffer.vertex(positionMatrix, (float)(vec3d2.x - normalize3.x * fireFlyFactor2), (float)(vec3d2.y - normalize3.y * fireFlyFactor2), (float)(vec3d2.z - normalize3.z * fireFlyFactor2))
                        .texture(0.5F, 1.0F)
                        .color(valueInt);
                     buffer.vertex(positionMatrix, (float)(vec3d3.x - normalize3.x * fireFlyFactor3), (float)(vec3d3.y - normalize3.y * fireFlyFactor3), (float)(vec3d3.z - normalize3.z * fireFlyFactor3))
                        .texture(0.5F, 1.0F)
                        .color(valueInt7);
                     buffer.vertex(positionMatrix, (float)(vec3d3.x + normalize3.x * fireFlyFactor3), (float)(vec3d3.y + normalize3.y * fireFlyFactor3), (float)(vec3d3.z + normalize3.z * fireFlyFactor3))
                        .texture(0.5F, 0.0F)
                        .color(valueInt7);
                  }
               }
            }
         }

         matrices.pop();
         this.immediate.draw(apply);
         VertexConsumer vertices = this.immediate.getBuffer(apply);

         for (int m = 0; m < this.firefliesThemeItems.size(); m++) {
            FireFly.FireFlyEntity firefliesthemeitem2 = this.firefliesThemeItems.get(m);
            float time4 = firefliesthemeitem2.getTime();
            if (!(time4 <= 0.01F)) {
               int matrixValue = booleanValue ? this.getReleaseRight(m) : firefliesthemeitem2.colorArgb;
               float fireFlyValue9 = firefliesthemeitem2.getTimeTimeMillis() * time4;
               double lerp4 = firefliesthemeitem2.getLerp(tickProgress);
               double lerpForFloat5 = firefliesthemeitem2.getLerpForFloat(tickProgress);
               double lerpStartLerp6 = firefliesthemeitem2.getLerpStartLerp(tickProgress);
               matrices.push();
               matrices.translate(lerp4 - cameraPos.x, lerpForFloat5 - cameraPos.y, lerpStartLerp6 - cameraPos.z);
               matrices.multiply(mc.gameRenderer.getCamera().getRotation());
               matrices.translate(0.0F, 0.0F, 0.02F);
               Matrix4f matrix = matrices.peek().getPositionMatrix();
               handleVertexTexture(vertices, matrix, 1.7F, MatrixAmountUtils.getValueInt(matrixValue, fireFlyValue9 * 0.15F));
               handleVertexTexture(vertices, matrix, 0.95F, MatrixAmountUtils.getValueInt(matrixValue, fireFlyValue9 * 0.4F));
               handleVertexTexture(vertices, matrix, 0.4F, MatrixAmountUtils.getValueInt(matrixValue, fireFlyValue9 * 0.85F));
               handleVertexTexture(vertices, matrix, 0.15F, MatrixAmountUtils.getValueInt(-1, fireFlyValue9));
               matrices.pop();
            }
         }

         this.immediate.draw();
      }
   }

   public FireFly() {
      super("FireFly", IconsPngType.VISUAL);
      this.random = new Random();
      this.addSettings(new ConfigSetting[]{this.integerHelper, this.booleanBase});
   }

   @Override
   public void onReset() {
      if (this.isEnabled() && mc.player != null && mc.world != null) {
         Vec3d entityPos = mc.player.getEntityPos();
         float iIiValue = 0.525F;
         this.firefliesThemeItems.forEach(maxSpeed -> maxSpeed.handleClampAmount(0.35F, iIiValue, entityPos));
         this.firefliesThemeItems.removeIf(fireFlyEntity -> fireFlyEntity.canAmount(entityPos));
         int iIiLimit = this.integerHelper.getValue();

         while (this.firefliesThemeItems.size() > iIiLimit) {
            this.firefliesThemeItems.remove(this.firefliesThemeItems.size() - 1);
         }

         while (this.firefliesThemeItems.size() < iIiLimit) {
            this.handleRandomFireflies(entityPos);
         }
      }
   }

   private int getReleaseRight(int releaseRightValue) {
      return MatrixAmountUtils.getValue(MatrixEspUtils.releaseRightValue, MatrixEspUtils.releaseRightAccentColor, 0.5F, releaseRightValue * 0.08F);
   }

   @Override
   public void onDisable() {
      this.firefliesThemeItems.clear();
      if (this.bufferAllocator != null) {
         this.bufferAllocator.close();
         this.bufferAllocator = null;
         this.immediate = null;
      }
   }

   private static class FireFlyEntity {
      final int colorArgb;
      double currentY;
      double clampCount;
      double clamp;
      double fireFlyCount;
      final long timeMillis = System.currentTimeMillis();
      final List<FireFly.TrailPoint> clampTrailPointList;
      final int clampValue;
      double valueClamp;
      double clampAmount;
      double startClamp;
      double endClamp;
      double startLerp;
      double lerpAmount;
      double endLerp;
      double targetOffset;
      final Random random;
      long lastTimeMillis = System.currentTimeMillis();

      void handleClampAmount(float clampFactor, float clampValue, Vec3d vec) {
         this.lerpAmount = this.endLerp;
         this.startClamp = this.currentY;
         this.startLerp = this.endClamp;
         if (System.currentTimeMillis() - this.lastTimeMillis > 2000 + this.random.nextInt(2000)) {
            double angle = Math.toRadians(this.random.nextDouble() * 360.0);
            double radians = Math.toRadians((this.random.nextDouble() - 0.5) * 40.0);
            this.fireFlyCount = -Math.sin(angle) * Math.cos(radians) * clampFactor;
            this.clampCount = Math.sin(radians) * clampFactor * 0.3;
            this.targetOffset = Math.cos(angle) * Math.cos(radians) * clampFactor;
            this.lastTimeMillis = System.currentTimeMillis();
         }

         double clampFactor2 = vec.x - this.endLerp;
         double clampFactor3 = vec.y + 1.0 - this.currentY;
         double clampFactor4 = vec.z - this.endClamp;
         double distance = Math.sqrt(clampFactor2 * clampFactor2 + clampFactor3 * clampFactor3 + clampFactor4 * clampFactor4);
         if (distance > 35.0) {
            this.fireFlyCount += clampFactor2 / distance * clampFactor * 0.15;
            this.clampCount += clampFactor3 / distance * clampFactor * 0.15;
            this.targetOffset += clampFactor4 / distance * clampFactor * 0.15;
         }

         double clampFactor5 = 0.02;
         this.clampAmount = this.clampAmount + (this.fireFlyCount - this.clampAmount) * clampFactor5;
         this.clamp = this.clamp + (this.clampCount - this.clamp) * clampFactor5;
         this.valueClamp = this.valueClamp + (this.targetOffset - this.valueClamp) * clampFactor5;
         double clampFactor6 = 0.03;
         this.clampAmount = this.clampAmount + (this.random.nextDouble() - 0.5) * clampFactor6;
         this.clamp = this.clamp + (this.random.nextDouble() - 0.5) * clampFactor6;
         this.valueClamp = this.valueClamp + (this.random.nextDouble() - 0.5) * clampFactor6;
         this.clampAmount = MathHelper.clamp(this.clampAmount, (double)(-clampValue), (double)clampValue);
         this.clamp = MathHelper.clamp(this.clamp, (double)(-clampValue), (double)clampValue);
         this.valueClamp = MathHelper.clamp(this.valueClamp, (double)(-clampValue), (double)clampValue);
         this.endLerp = this.endLerp + this.clampAmount;
         this.currentY = this.currentY + this.clamp;
         this.endClamp = this.endClamp + this.valueClamp;
         this.clampTrailPointList.add(0, new FireFly.TrailPoint(this.endLerp, this.currentY, this.endClamp));

         while (this.clampTrailPointList.size() > this.clampValue) {
            this.clampTrailPointList.remove(this.clampTrailPointList.size() - 1);
         }
      }

      double getLerp(float lerpValue) {
         return MathHelper.lerp((double)lerpValue, this.lerpAmount, this.endLerp);
      }

      double getLerpForFloat(float lerpValue) {
         return MathHelper.lerp((double)lerpValue, this.startClamp, this.currentY);
      }

      double getLerpStartLerp(float lerpValue) {
         return MathHelper.lerp((double)lerpValue, this.startLerp, this.endClamp);
      }

      boolean canAmount(Vec3d vec) {
         double fireFlyFactor = this.endLerp - vec.x;
         double fireFlyFactor2 = this.currentY - vec.y;
         double fireFlyFactor3 = this.endClamp - vec.z;
         return fireFlyFactor * fireFlyFactor + fireFlyFactor2 * fireFlyFactor2 + fireFlyFactor3 * fireFlyFactor3 > 6400.0;
      }

      float getTime() {
         long fireFlyValue = System.currentTimeMillis() - this.timeMillis;
         return fireFlyValue < 1000L ? (float)fireFlyValue / 1000.0F : 1.0F;
      }

      FireFlyEntity(double amount, double amount2, double amount3, double amount4, double amount5, double amount6, int value, int value2) {
         this.clampTrailPointList = new ArrayList<>();
         this.random = new Random();
         this.endLerp = this.lerpAmount = amount;
         this.currentY = this.startClamp = amount2;
         this.endClamp = this.startLerp = amount3;
         this.clampAmount = this.fireFlyCount = amount4;
         this.clamp = this.clampCount = amount5;
         this.valueClamp = this.targetOffset = amount6;
         this.colorArgb = value;
         this.clampValue = value2;
      }

      float getTimeTimeMillis() {
         double fireFlyValue = 0.8 + 0.2 * Math.sin((System.currentTimeMillis() - this.timeMillis) / 200.0);
         return (float)fireFlyValue;
      }
   }

   private static class TrailPoint {
      final double posZ;
      final double trailPointAmount;
      final double posY;

      TrailPoint(double amount, double amount2, double amount3) {
         this.trailPointAmount = amount;
         this.posY = amount2;
         this.posZ = amount3;
      }
   }
}
