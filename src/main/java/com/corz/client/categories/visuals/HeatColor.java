package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import java.awt.Color;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class HeatColor extends Module {
   private final BooleanSetting booleanBase;
   private final BooleanSetting playersOnly;
   private final DoubleSetting doubleHelper = new DoubleSetting("Range", "Max distance to tint", 24.0, 4.0, 64.0, 1.0);

   public HeatColor() {
      super("Heat Color", IconsPngType.VISUAL);
      this.playersOnly = new BooleanSetting("Players Only", "Only tint other players", true);
      this.booleanBase = new BooleanSetting("Filled", "Also draw a translucent fill", false);
      this.addSettings(new ConfigSetting[]{this.doubleHelper, this.playersOnly, this.booleanBase});
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null) {
         Camera camera = mc.gameRenderer.getCamera();
         if (camera != null) {
            Vec3d cameraPos = camera.getCameraPos();
            Vec3d eyePos = mc.player.getEyePos();
            double heatColorValue = this.doubleHelper.getValue() * this.doubleHelper.getValue();
            float tickProgress = queueCommand.tickProgress;
            queueCommand.matrices.push();
            queueCommand.matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            Matrix4f positionMatrix = queueCommand.matrices.peek().getPositionMatrix();
            boolean heatColorFlag = false;

            for (Entity entity : mc.world.getEntities()) {
               if ((entity != mc.player || ClampMatrixUtils.isInstanceScreen())
                  && entity instanceof LivingEntity livingEntity
                  && livingEntity.isAlive()
                  && (!this.playersOnly.isEnabled() || entity instanceof PlayerEntity)
                  && !(eyePos.squaredDistanceTo(livingEntity.getEyePos()) > heatColorValue)) {
                  float heatColorValue2 = livingEntity.getMaxHealth() <= 0.0F ? 1.0F : MathHelper.clamp(livingEntity.getHealth() / livingEntity.getMaxHealth(), 0.0F, 1.0F);
                  Color hsbColor = Color.getHSBColor(heatColorValue2 * 0.333F, 1.0F, 1.0F);
                  int n2 = 0xFF000000 | hsbColor.getRGB() & 16777215;
                  Vec3d lerpedPos = livingEntity.getLerpedPos(tickProgress);
                  float widthScaled = livingEntity.getWidth() / 2.0F;
                  float height = livingEntity.getHeight();
                  float vertexBuilderValue = (float)(lerpedPos.x - widthScaled);
                  float y = (float)lerpedPos.y;
                  float amountForDouble = (float)(lerpedPos.z - widthScaled);
                  float vertexColorValue = (float)(lerpedPos.x + widthScaled);
                  float f5 = (float)(lerpedPos.y + height);
                  float f6 = (float)(lerpedPos.z + widthScaled);
                  if (this.booleanBase.isEnabled()) {
                     VertexBuilderUtils.handle(
                        positionMatrix, vertexBuilderValue, y, amountForDouble, vertexColorValue, f5, f6, hsbColor.getRed() / 255.0F, hsbColor.getGreen() / 255.0F, hsbColor.getBlue() / 255.0F, 0.25F
                     );
                  }

                  VertexBuilderUtils.handle(positionMatrix, vertexBuilderValue, y, amountForDouble, vertexColorValue, f5, f6, n2, 1.5F);
                  heatColorFlag = true;
               }
            }

            queueCommand.matrices.pop();
            if (heatColorFlag) {
               VertexBuilderUtils.updateDrawImmediate();
            }
         }
      }
   }
}
