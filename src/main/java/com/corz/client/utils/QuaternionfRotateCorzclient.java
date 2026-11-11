package com.corz.client.utils;

import com.corz.client.gui.MatrixMatricesScreen;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.mixins.AccessorInventoryScreen;
import com.corz.client.render.CorzRoundedUtils;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.corz.client.categories.misc.SkinProtect;
import com.corz.client.categories.misc.CustomSkin;

public final class QuaternionfRotateCorzclient {
   private double lastAngleDegrees;
   private boolean quaternionfRotateActive;
   private float corzclientScaleAmount;
   private double quaternionfRotateAmount;
   private float bodyYawOffsetDegrees = 0.0F;

   public boolean canAmountFlag(double amount, double amount2, int quaternionfRotateValue, int quaternionfRotateValue2, int quaternionfRotateValue3, int quaternionfRotateValue4, int quaternionfRotateValue5) {
      if (quaternionfRotateValue5 == 0 && amount >= quaternionfRotateValue && amount <= quaternionfRotateValue + quaternionfRotateValue3 && amount2 >= quaternionfRotateValue2 && amount2 <= quaternionfRotateValue2 + quaternionfRotateValue4) {
         this.quaternionfRotateActive = true;
         this.lastAngleDegrees = amount;
         this.quaternionfRotateAmount = amount2;
         return true;
      } else {
         return false;
      }
   }

   public void renderCorzclientScale(DrawContext context, int corzclientScaleValue, int editValue, int releaseRightLimit, int n4, int corzclientValue) {
      MatrixPeekUtilsRender.renderMatrixStack(context, corzclientScaleValue, editValue, releaseRightLimit, n4, 6, CorzRoundedUtils.getPanelOverlay(new Color(255, 255, 255, 18).getRGB()));
      MatrixPeekUtilsRender.renderMatrixStack(context, corzclientScaleValue + 1, editValue + 1, releaseRightLimit - 2, n4 - 2, 5, CorzRoundedUtils.getPanelOverlay(new Color(12, 12, 16, 236).getRGB()));
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player != null) {
         if (!MatrixMatricesScreen.isMatrixMatricesActive()) {
            int max5 = Math.max(20, (int)(n4 * 0.46F));
            int corzclientValue2 = SkinProtect.corzclientValue;
            SkinProtect.corzclientValue = corzclientValue;
            int value = CustomSkin.scaleValue;
            CustomSkin.scaleValue = -2;

            try {
               EntityRenderState corzBuildEntityState = AccessorInventoryScreen.corzBuildEntityState(instance.player);
               Quaternionf rotateZ = new Quaternionf().rotateZ((float) Math.PI);
               Quaternionf rotateX = new Quaternionf().rotateX(this.corzclientScaleAmount * (float) (Math.PI / 180.0));
               rotateZ.mul(rotateX);
               if (corzBuildEntityState instanceof LivingEntityRenderState livingEntityRenderState) {
                  livingEntityRenderState.bodyYaw = 180.0F + this.bodyYawOffsetDegrees;
                  livingEntityRenderState.relativeHeadYaw = 0.0F;
                  livingEntityRenderState.pitch = 0.0F;
                  if (livingEntityRenderState.baseScale != 0.0F) {
                     livingEntityRenderState.width = livingEntityRenderState.width / livingEntityRenderState.baseScale;
                     livingEntityRenderState.height = livingEntityRenderState.height / livingEntityRenderState.baseScale;
                     livingEntityRenderState.baseScale = 1.0F;
                  }
               }

               Vector3f vector3f = new Vector3f(0.0F, corzBuildEntityState.height / 2.0F, 0.0F);
               context.addEntity(corzBuildEntityState, max5, vector3f, rotateZ, rotateX, corzclientScaleValue + 2, editValue + 2, corzclientScaleValue + releaseRightLimit - 2, editValue + n4 - 2);
            } catch (Exception e) {
            } finally {
               SkinProtect.corzclientValue = corzclientValue2;
               CustomSkin.scaleValue = value;
            }
         }
      }
   }

   public void handleAmountFlag(double amount, double amount2) {
      if (this.quaternionfRotateActive) {
         this.bodyYawOffsetDegrees = RotateQuaternionfUtils.getAmount(this.bodyYawOffsetDegrees + RotateQuaternionfUtils.getAmount(amount - this.lastAngleDegrees));
         this.corzclientScaleAmount = RotateQuaternionfUtils.getAmountForFloat(this.corzclientScaleAmount + RotateQuaternionfUtils.getAmountForDouble(amount2 - this.quaternionfRotateAmount));
         this.lastAngleDegrees = amount;
         this.quaternionfRotateAmount = amount2;
      }
   }

   public QuaternionfRotateCorzclient() {
      this.corzclientScaleAmount = 0.0F;
      this.quaternionfRotateActive = false;
   }

   public void updateFlag() {
      this.quaternionfRotateActive = false;
   }
}
