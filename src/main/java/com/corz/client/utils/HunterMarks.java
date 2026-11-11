package com.corz.client.utils;

import com.corz.client.CorzClient;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.VectorPlaneUtils;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import com.corz.client.render.VertexTexturePeek;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import com.corz.client.utils.BuildGuideOverlay;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.utils.PearlHop;

public final class HunterMarks {
   private final int[] arcColors;
   private final BuildGuideOverlay frameTracker = new BuildGuideOverlay();
   private final double[] arcPoints;
   private boolean disabled;
   private final MatrixStack matrixStack = new MatrixStack();

   private void renderBeam(BlockPos pos) {
      MinecraftClient instance = MinecraftClient.getInstance();
      Vec3d cameraPos = instance.gameRenderer.getCamera().getCameraPos();
      double cameraClampValue = pos.getX() + 0.5;
      double vertexBuilderValue = pos.getY() + 1.0;
      double cameraClampValue3 = pos.getZ() + 0.5;
      double cameraClampFactor = cameraClampValue - cameraPos.x;
      double cameraClampFactor2 = cameraClampValue3 - cameraPos.z;
      double distance = Math.sqrt(cameraClampFactor * cameraClampFactor + cameraClampFactor2 * cameraClampFactor2);
      if (!(distance < 2.0)) {
         Matrix4f matrix = VertexBuilderUtils.getMatrix();
         VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();
         int cameraClampValue4 = MatrixEspUtils.releaseRightValue & 16777215;
         float cameraClampValue5 = 0.8F + 0.2F * (float)Math.sin(System.currentTimeMillis() % 10000L / 1000.0 * 2.2);
         float cameraClampFactor3 = (float)(cameraClampFactor2 / distance);
         float cameraClampFactor4 = (float)(-cameraClampFactor / distance);
         float cameraClampFactor5 = 0.11F * (float)MathHelper.clamp(distance / 32.0, 1.0, 5.0);
         float amountX = VertexBuilderUtils.getAmountX(cameraClampValue);
         float amountForDouble = VertexBuilderUtils.getAmountForDouble(cameraClampValue3);
         float cameraClampValue8 = VertexBuilderUtils.getAmount(vertexBuilderValue);
         float deltaY = VertexBuilderUtils.getAmount(vertexBuilderValue + 40.0);
         int valueInt = MatrixAmountUtils.getValueInt(cameraClampValue4 | 0xFF000000, 0.55F * cameraClampValue5);
         VertexBuilderUtils.handleVertexColor(
            buffer,
            matrix,
            amountX + cameraClampFactor3 * cameraClampFactor5,
            cameraClampValue8,
            amountForDouble + cameraClampFactor4 * cameraClampFactor5,
            valueInt,
            amountX - cameraClampFactor3 * cameraClampFactor5,
            cameraClampValue8,
            amountForDouble - cameraClampFactor4 * cameraClampFactor5,
            valueInt,
            amountX - cameraClampFactor3 * cameraClampFactor5,
            deltaY,
            amountForDouble - cameraClampFactor4 * cameraClampFactor5,
            cameraClampValue4,
            amountX + cameraClampFactor3 * cameraClampFactor5,
            deltaY,
            amountForDouble + cameraClampFactor4 * cameraClampFactor5,
            cameraClampValue4
         );
      }
   }

   public void submitTarget(float posValue, boolean flag, BlockPos blockPos, List<BlockPos> items, BlockPos blockPos2, PearlHop.Shot shot) {
      if (!this.disabled) {
         try {
            BuildGuideOverlay.Frame minMax = this.frameTracker.getFrame();
            minMax.clear();
            minMax.trailEnabled = flag;
            minMax.guidesEnabled = flag;
            if (blockPos != null) {
               minMax.setCutBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
               minMax.cellBlock = blockPos;
            }

            if (items != null && !items.isEmpty() && blockPos2 != null) {
               minMax.pathPositions = items;
               minMax.targetBlock = blockPos2;
               minMax.targetActive = true;
            }

            this.frameTracker.renderGuidesSafely(flag, posValue);
            if (!flag || blockPos == null) {
               return;
            }

            this.renderBeam(blockPos);
            if (shot != null && shot.arc() != null) {
               this.renderPearlArc(shot);
            }
         } catch (Throwable e) {
            this.disabled = true;
         }
      }
   }

   public void renderTargetLabel(DrawContext context, BlockPos pos, String scaledWidthText, String corzclientText) {
      if (!this.disabled && context != null && pos != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance.player != null && instance.currentScreen == null) {
            CorzClient.fontManager.setMatrixStack(this.matrixStack);
            MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
            double scaleFactor = instance.getWindow().getScaleFactor();
            int calculateScaleFactor = instance.getWindow().calculateScaleFactor(ShowHUD.getMax(instance), instance.forcesUnicodeFont());
            float scaledWidthDivisor = (float)(calculateScaleFactor / scaleFactor);
            boolean scaledWidthFlag = Math.abs(scaledWidthDivisor - 1.0F) > 1.0E-4F;
            if (scaledWidthFlag) {
               CorzRoundedUtils.scaledWidth = Math.round(instance.getWindow().getScaledWidth() / scaledWidthDivisor);
               CorzRoundedUtils.scaledHeight = Math.round(instance.getWindow().getScaledHeight() / scaledWidthDivisor);
            }

            try {
               VertexTexturePeek regular = VertexTexturePeek.getRegular();
               VertexTexturePeek vertexTexturePeek = regular.isCorzclientFlag() ? regular : VertexTexturePeek.getSuisse();
               if (!vertexTexturePeek.isCorzclientFlag()) {
                  return;
               }

               Vec3d windowFramebuffer = VertexConsumerUtilsVector.getWindowFramebuffer(new Vec3d(pos.getX() + 0.5, pos.getY() + 1.35, pos.getZ() + 0.5));
               if (windowFramebuffer == null || windowFramebuffer.z <= 0.0 || windowFramebuffer.z >= 1.0) {
                  return;
               }

               float scaledWidthValue = (float)(windowFramebuffer.x / scaledWidthDivisor);
               float scaledWidthValue2 = (float)(windowFramebuffer.y / scaledWidthDivisor);
               float scaledWidthValue3 = 18.0F;
               float scaledWidthValue4 = 7.0F;
               float corzclientValue = 11.0F;
               float corzLicenseLimit = 5.0F;
               String scaledWidthText3 = "  ";
               float corzclientAmounts = vertexTexturePeek.getCorzclientAmounts(scaledWidthText, corzclientValue);
               float corzclientAmounts2 = vertexTexturePeek.getCorzclientAmounts(scaledWidthText3, corzclientValue);
               float scaledWidthLimit = corzclientText != null && !corzclientText.isEmpty() ? vertexTexturePeek.getCorzclientAmounts(corzclientText, corzclientValue) : 0.0F;
               float materialFactor = scaledWidthValue4 + corzclientAmounts + (scaledWidthLimit > 0.0F ? corzclientAmounts2 + scaledWidthLimit : 0.0F) + scaledWidthValue4;
               float matrixPeekValue = scaledWidthValue - materialFactor / 2.0F;
               float deltaVar = scaledWidthValue2 - scaledWidthValue3;
               MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, deltaVar, materialFactor, scaledWidthValue3, corzLicenseLimit, -233499369, 620756991);
               float scaledWidthValue9 = deltaVar + (scaledWidthValue3 - vertexTexturePeek.getAmountForFloat(corzclientValue)) / 2.0F;
               vertexTexturePeek.handlePeekEnd(scaledWidthText, matrixPeekValue + scaledWidthValue4, scaledWidthValue9, 0xFF000000 | MatrixEspUtils.releaseRightValue, corzclientValue);
               if (scaledWidthLimit > 0.0F) {
                  vertexTexturePeek.handlePeekEnd(corzclientText, matrixPeekValue + scaledWidthValue4 + corzclientAmounts + corzclientAmounts2, scaledWidthValue9, -6645087, corzclientValue);
               }
            } finally {
               if (scaledWidthFlag) {
                  CorzRoundedUtils.scaledWidth = 0.0F;
                  CorzRoundedUtils.scaledHeight = 0.0F;
               }
            }
         }
      }
   }

   private void renderPearlArc(PearlHop.Shot shot) {
      MinecraftClient instance = MinecraftClient.getInstance();
      Camera camera = instance.gameRenderer.getCamera();
      if (camera != null) {
         List arc = shot.arc();
         int cameraLimit = Math.min(256, arc.size());
         if (cameraLimit >= 2) {
            int cameraValue = 0xFF000000 | MatrixEspUtils.releaseRightValue;
            int matrixValue = CardMatrixUtils.getValue(cameraValue, -1, 0.55);

            for (int i = 0; i < cameraLimit; i++) {
               Vec3d vec3d = (Vec3d)arc.get(i);
               this.arcPoints[i * 3] = vec3d.x;
               this.arcPoints[i * 3 + 1] = vec3d.y;
               this.arcPoints[i * 3 + 2] = vec3d.z;
               float amountForFloat = MatrixAmountUtils.getAmountForFloat(Math.min(1.0F, i / 6.0F));
               this.arcColors[i] = MatrixAmountUtils.getValueInt(matrixValue, 0.85F * amountForFloat);
            }

            float max4 = Math.max(1.0F, instance.getWindow().getFramebufferHeight() / 1080.0F);
            VectorPlaneUtils.handleAmountsPlane(
               VertexBuilderUtils.getBufferIMMEDIATE(), VertexBuilderUtils.getMatrix(), camera, this.arcPoints, this.arcColors, cameraLimit, 2.2F * max4, 1.0F, false, false
            );
         }
      }
   }

   public HunterMarks() {
      this.arcPoints = new double[768];
      this.arcColors = new int[256];
   }
}
