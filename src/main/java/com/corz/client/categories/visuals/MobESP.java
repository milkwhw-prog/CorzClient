package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class MobESP extends Module {
   private final EnumSetting enumHelper;
   private static MobESP singleton;
   private int cornersOutlineValue;
   private int mobEspValue;
   private int cornersOutlineValueCornersOu;
   private final EnumSetting mobsCornersOutline = new EnumSetting("Mobs", "Which mobs to highlight", "All", "Hostile", "Passive", "All");

   static boolean canFlag(int mobEspValue, int groupSpawn) {
      return mobEspValue == 2 || mobEspValue == groupSpawn - 1;
   }

   static int getGroupSpawn(Entity entity) {
      if (entity instanceof MobEntity && entity.isAlive()) {
         return !(entity instanceof Monster) && entity.getType().getSpawnGroup() != SpawnGroup.MONSTER ? 2 : 1;
      } else {
         return 0;
      }
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null) {
         int indexOf = this.enumHelper.getCurrentIndex();
         if (indexOf != 0) {
            this.updateCornersOutline();
            Vec3d cameraPos = VertexConsumerUtilsVector.getCameraPos();
            double x = cameraPos.x;
            double y = cameraPos.y;
            double z = cameraPos.z;
            Matrix4f positionMatrix = queueCommand.matrices.peek().getPositionMatrix();
            float tickProgress = queueCommand.tickProgress;
            int currentIndex = this.mobsCornersOutline.getCurrentIndex();
            int mobEspCounter = 0;

            for (Entity entity : mc.world.getEntities()) {
               int groupSpawn = getGroupSpawn(entity);
               if (groupSpawn != 0 && canFlag(currentIndex, groupSpawn)) {
                  double mobEspValue = MathHelper.lerp((double)tickProgress, entity.lastX, entity.getX()) - entity.getX();
                  double mobEspValue2 = MathHelper.lerp((double)tickProgress, entity.lastY, entity.getY()) - entity.getY();
                  double mobEspValue3 = MathHelper.lerp((double)tickProgress, entity.lastZ, entity.getZ()) - entity.getZ();
                  Box boundingBox = entity.getBoundingBox();
                  double vectorValue = (boundingBox.minX + boundingBox.maxX) * 0.5 + mobEspValue;
                  double destinationAmount = (boundingBox.minY + boundingBox.maxY) * 0.5 + mobEspValue2;
                  double destinationZ = (boundingBox.minZ + boundingBox.maxZ) * 0.5 + mobEspValue3;
                  double mobEspFactor = boundingBox.maxX - boundingBox.minX;
                  double mobEspFactor2 = boundingBox.maxY - boundingBox.minY;
                  double mobEspFactor3 = boundingBox.maxZ - boundingBox.minZ;
                  float height = (float)(Math.sqrt(mobEspFactor * mobEspFactor + mobEspFactor2 * mobEspFactor2 + mobEspFactor3 * mobEspFactor3) * 0.5) + 0.5F;
                  if (!VertexBuilderUtils.canVector(vectorValue, destinationAmount, destinationZ, height)) {
                     float vertexBuilderValue = (float)(boundingBox.minX + mobEspValue - x);
                     float f2 = (float)(boundingBox.minY + mobEspValue2 - y);
                     float mobEspValue10 = (float)(boundingBox.minZ + mobEspValue3 - z);
                     float vertexColorValue = (float)(boundingBox.maxX + mobEspValue - x);
                     float f5 = (float)(boundingBox.maxY + mobEspValue2 - y);
                     float amountForDouble = (float)(boundingBox.maxZ + mobEspValue3 - z);
                     int mobEspValue14 = groupSpawn == 1 ? this.mobEspValue : this.cornersOutlineValue;
                     int n2 = 0xFF000000 | mobEspValue14;
                     if (indexOf == 1) {
                        VertexBuilderUtils.handle(
                           positionMatrix,
                           vertexBuilderValue,
                           f2,
                           mobEspValue10,
                           vertexColorValue,
                           f5,
                           amountForDouble,
                           (mobEspValue14 >> 16 & 0xFF) / 255.0F,
                           (mobEspValue14 >> 8 & 0xFF) / 255.0F,
                           (mobEspValue14 & 0xFF) / 255.0F,
                           0.2F
                        );
                        VertexBuilderUtils.handle(positionMatrix, vertexBuilderValue, f2, mobEspValue10, vertexColorValue, f5, amountForDouble, n2, 1.6F);
                     } else {
                        handle(positionMatrix, vertexBuilderValue, f2, mobEspValue10, vertexColorValue, f5, amountForDouble, n2, 2.2F);
                     }

                     mobEspCounter++;
                  }
               }
            }

            if (mobEspCounter > 0) {
               VertexBuilderUtils.updateDrawImmediate();
            }
         }
      }
   }

   static int getValue(int mobEspValue, int mobEspValue2, float mobEspFactor) {
      int round = Math.round((mobEspValue >> 16 & 0xFF) * (1.0F - mobEspFactor) + (mobEspValue2 >> 16 & 0xFF) * mobEspFactor);
      int round4 = Math.round((mobEspValue >> 8 & 0xFF) * (1.0F - mobEspFactor) + (mobEspValue2 >> 8 & 0xFF) * mobEspFactor);
      int round5 = Math.round((mobEspValue & 0xFF) * (1.0F - mobEspFactor) + (mobEspValue2 & 0xFF) * mobEspFactor);
      return round << 16 | round4 << 8 | round5;
   }

   private static void handle(Matrix4f matrix, float mobEspValue, float f2, float amountForDouble, float vertexColorValue, float f5, float mobEspValue6, int n2, float z22) {
      float mobEspValue9 = (vertexColorValue - mobEspValue) * 0.25F;
      float mobEspValue10 = (f5 - f2) * 0.25F;
      float mobEspValue11 = (mobEspValue6 - amountForDouble) * 0.25F;

      for (int i = 0; i < 8; i++) {
         boolean mobEspFlag = (i & 1) == 0;
         boolean mobEspFlag2 = (i & 2) == 0;
         boolean mobEspFlag3 = (i & 4) == 0;
         float vertexBuilderValue = mobEspFlag ? mobEspValue : vertexColorValue;
         float y = mobEspFlag2 ? f2 : f5;
         float mobEspValue14 = mobEspFlag3 ? amountForDouble : mobEspValue6;
         float mobEspValue15 = mobEspFlag ? mobEspValue9 : -mobEspValue9;
         float mobEspValue16 = mobEspFlag2 ? mobEspValue10 : -mobEspValue10;
         float mobEspValue17 = mobEspFlag3 ? mobEspValue11 : -mobEspValue11;
         VertexBuilderUtils.handleForMatrix4fFloat(matrix, vertexBuilderValue, y, mobEspValue14, vertexBuilderValue + mobEspValue15, y, mobEspValue14, n2, z22);
         VertexBuilderUtils.handleForMatrix4fFloat(matrix, vertexBuilderValue, y, mobEspValue14, vertexBuilderValue, y + mobEspValue16, mobEspValue14, n2, z22);
         VertexBuilderUtils.handleForMatrix4fFloat(matrix, vertexBuilderValue, y, mobEspValue14, vertexBuilderValue, y, mobEspValue14 + mobEspValue17, n2, z22);
      }
   }

   public MobESP() {
      super("Mob ESP", IconsPngType.VISUAL);
      this.enumHelper = new EnumSetting("Style", "Glowing outline, see-through box, or box corners", "Outline Glow", "Outline Glow", "Box", "Corners");
      this.cornersOutlineValueCornersOu = Integer.MIN_VALUE;
      this.mobEspValue = 16729670;
      this.cornersOutlineValue = 3731016;
      this.addSettings(new ConfigSetting[]{this.mobsCornersOutline, this.enumHelper});
      singleton = this;
   }

   public static int getCornersOutline(Entity entity) {
      MobESP singleton2 = singleton;
      if (singleton2 != null && singleton2.isEnabled() && singleton2.enumHelper.getCurrentIndex() == 0) {
         int groupSpawn = getGroupSpawn(entity);
         if (groupSpawn != 0 && canFlag(singleton2.mobsCornersOutline.getCurrentIndex(), groupSpawn) && entity.getEntityWorld().isClient()) {
            singleton2.updateCornersOutline();
            return 0xFF000000 | (groupSpawn == 1 ? singleton2.mobEspValue : singleton2.cornersOutlineValue);
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private void updateCornersOutline() {
      int cornersOutlineValue = MatrixEspUtils.releaseRightValue & 16777215;
      if (cornersOutlineValue != this.cornersOutlineValueCornersOu) {
         this.cornersOutlineValueCornersOu = cornersOutlineValue;
         this.mobEspValue = getValue(16729670, cornersOutlineValue, 0.18F);
         this.cornersOutlineValue = getValue(3731016, cornersOutlineValue, 0.18F);
      }
   }
}
