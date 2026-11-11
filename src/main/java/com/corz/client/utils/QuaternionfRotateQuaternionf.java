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
import com.corz.client.categories.misc.CustomSkin;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.BrainrotTurnUtils;
import com.corz.client.utils.CorzclientWarm;

public final class QuaternionfRotateQuaternionf {
   private double previousInputAmount;
   private boolean quaternionfRotateActive;
   private double quaternionfRotateAmount;
   private float scaleAmount;
   private float bodyYawOffsetDegrees = 0.0F;

   public boolean canAmountFlag(double amount, double amount2, int quaternionfRotateValue, int quaternionfRotateValue2, int quaternionfRotateValue3, int quaternionfRotateValue4, int quaternionfRotateValue5) {
      if (quaternionfRotateValue5 == 0 && amount >= quaternionfRotateValue && amount <= quaternionfRotateValue + quaternionfRotateValue3 && amount2 >= quaternionfRotateValue2 && amount2 <= quaternionfRotateValue2 + quaternionfRotateValue4) {
         this.quaternionfRotateActive = true;
         this.quaternionfRotateAmount = amount;
         this.previousInputAmount = amount2;
         return true;
      } else {
         return false;
      }
   }

   public QuaternionfRotateQuaternionf() {
      this.scaleAmount = 0.0F;
      this.quaternionfRotateActive = false;
   }

   public void updateFlag() {
      this.quaternionfRotateActive = false;
   }

   public void renderAmountScale(DrawContext context, int scaleValue, int editValue, int releaseRightLimit, int n4, int value) {
      MatrixPeekUtilsRender.renderMatrixStack(context, scaleValue, editValue, releaseRightLimit, n4, 6, CorzRoundedUtils.getPanelOverlay(new Color(255, 255, 255, 18).getRGB()));
      MatrixPeekUtilsRender.renderMatrixStack(context, scaleValue + 1, editValue + 1, releaseRightLimit - 2, n4 - 2, 5, CorzRoundedUtils.getPanelOverlay(new Color(12, 12, 16, 236).getRGB()));
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player != null) {
         if (!MatrixMatricesScreen.isMatrixMatricesActive()) {
            float scaleFactor = 0.05859375F;
            float scaleValue5 = 1.8F;
            float scaleValue6 = 0.0F;
            float scaleBound = 0.3F;
            WarmBrainrotBase brainrot_turn_item = BrainrotTurnUtils.BRAINROT_TURN_ITEMS.get(Math.floorMod(value, BrainrotTurnUtils.BRAINROT_TURN_ITEMS.size()));
            CorzclientWarm classCorzclientWarm = brainrot_turn_item.getClassCorzclientWarm();
            if (classCorzclientWarm == null) {
               brainrot_turn_item.updateThrowableThread();
            } else {
               scaleValue5 = Math.max(classCorzclientWarm.boundsMaxY, 32.0F) * scaleFactor;
               scaleValue6 = Math.min(classCorzclientWarm.corzclientWarmAmount, 0.0F) * scaleFactor;
               float maxAbs = Math.max(Math.abs(classCorzclientWarm.boundsMaxX), Math.abs(classCorzclientWarm.scaleAmount));
               float max2 = Math.max(Math.abs(classCorzclientWarm.boundsMinZ), Math.abs(classCorzclientWarm.boundsMaxZ));
               scaleBound = (float)Math.sqrt(maxAbs * maxAbs + max2 * max2) * scaleFactor;
            }

            float hashHuntBound = Math.min((n4 - 6) * 0.86F / (scaleValue5 - scaleValue6), (releaseRightLimit - 6) * 0.86F / (2.0F * Math.max(0.3F, scaleBound)));
            int max7 = Math.max(16, (int)Math.min(n4 * 0.46F, hashHuntBound));
            int scaleValue2 = CustomSkin.scaleValue;
            CustomSkin.scaleValue = value;

            try {
               EntityRenderState corzBuildEntityState = AccessorInventoryScreen.corzBuildEntityState(instance.player);
               Quaternionf rotateZ = new Quaternionf().rotateZ((float) Math.PI);
               Quaternionf rotateX = new Quaternionf().rotateX(this.scaleAmount * (float) (Math.PI / 180.0));
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

               Vector3f vector3f = new Vector3f(0.0F, (scaleValue5 + scaleValue6) / 2.0F, 0.0F);
               context.addEntity(corzBuildEntityState, max7, vector3f, rotateZ, rotateX, scaleValue + 2, editValue + 2, scaleValue + releaseRightLimit - 2, editValue + n4 - 2);
            } catch (Exception e) {
            } finally {
               CustomSkin.scaleValue = scaleValue2;
            }
         }
      }
   }

   public void handleAmountFlag(double amount, double amount2) {
      if (this.quaternionfRotateActive) {
         this.bodyYawOffsetDegrees = RotateQuaternionfUtils.getAmount(this.bodyYawOffsetDegrees + RotateQuaternionfUtils.getAmount(amount - this.quaternionfRotateAmount));
         this.scaleAmount = RotateQuaternionfUtils.getAmountForFloat(this.scaleAmount + RotateQuaternionfUtils.getAmountForDouble(amount2 - this.previousInputAmount));
         this.quaternionfRotateAmount = amount;
         this.previousInputAmount = amount2;
      }
   }
}
