package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class BlockHighlight extends Module {
   private long timeMillis;
   private final BooleanSetting booleanBase = new BooleanSetting("Client Color", "Use the client theme colour instead of custom colours", true);
   private double growAmount;
   private BlockPos blockPos;
   private final EnumSetting enumHelper;
   private double maxZ;
   private double growAmount2Grow;
   private double growAmount3Grow;
   private double minZ;
   private VoxelShape outlineShape;
   private final ColorSetting colorBase;
   private double maxY;
   private double maxX;
   private double growAmountGrow;
   private float growProgress;
   private final ColorSetting outlineGrow = new ColorSetting("Outline", "Outline colour (when Client Color is off)", -922746881);
   private double blockHighlightAmount;
   private boolean growSlideFlag;
   private double minY;
   private double animatedMinX;
   private boolean speedSnapFlag;
   private final DoubleSetting doubleHelper;
   private double minX;
   private float speedSnapAmount;

   private void handleVertexColor(
      VertexConsumer vertices,
      Matrix4f matrix,
      float vertexColorValue,
      float blockHighlightFactor,
      float vertexColorValue3,
      float vertexColorFactor,
      float vertexColorValue5,
      float blockHighlightValue,
      float vertexColorValue7,
      float vertexColorValue8,
      float vertexColorValue9,
      float z22,
      float vertexColorValue11,
      float vertexColorValue12,
      float vertexColorValue13,
      float vertexColorValue14,
      float vertexColorValue15,
      float z12,
      float x12
   ) {
      int vertexColorValue18 = this.getValue(blockHighlightValue, blockHighlightFactor, vertexColorValue3, vertexColorFactor, vertexColorValue5);
      int vertexColorValue19 = this.getValue(vertexColorValue9, blockHighlightFactor, vertexColorValue3, vertexColorFactor, vertexColorValue5);
      int vertexColorValue20 = this.getValue(vertexColorValue12, blockHighlightFactor, vertexColorValue3, vertexColorFactor, vertexColorValue5);
      int vertexColorValue21 = this.getValue(vertexColorValue15, blockHighlightFactor, vertexColorValue3, vertexColorFactor, vertexColorValue5);
      if (vertexColorValue18 != 0 || vertexColorValue19 != 0 || vertexColorValue20 != 0 || vertexColorValue21 != 0) {
         vertices.vertex(matrix, blockHighlightValue, vertexColorValue7, vertexColorValue8).color(255, 253, 248, vertexColorValue18);
         vertices.vertex(matrix, vertexColorValue9, z22, vertexColorValue11).color(255, 253, 248, vertexColorValue19);
         vertices.vertex(matrix, vertexColorValue12, vertexColorValue13, vertexColorValue14).color(255, 253, 248, vertexColorValue20);
         vertices.vertex(matrix, vertexColorValue15, z12, x12).color(255, 253, 248, vertexColorValue21);
      }
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      if (this.isEnabled() && mc.world != null && mc.player != null) {
         BlockPos blockPos = null;
         if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == Type.BLOCK) {
            blockPos = ((BlockHitResult)mc.crosshairTarget).getBlockPos();
         }

         if (blockPos == null) {
            this.speedSnapFlag = false;
         } else {
            this.speedSnapFlag = true;
            if (this.blockPos == null || !this.blockPos.equals(blockPos)) {
               this.blockPos = blockPos;
               BlockState blockState = mc.world.getBlockState(blockPos);
               this.outlineShape = blockState.getOutlineShape(mc.world, blockPos);
               if (!this.outlineShape.isEmpty()) {
                  this.handleGrowMin(this.outlineShape.getBoundingBox().offset(blockPos));
               }
            }
         }

         this.handleGrowAmount(queueCommand);
      }
   }

   private void handleGrowAmount(QueueCommand queueCommand) {
      if (this.growSlideFlag && this.outlineShape != null && !this.outlineShape.isEmpty() && this.blockPos != null) {
         long timeMillis = System.currentTimeMillis();
         float growFactor = Math.min((float)(timeMillis - this.timeMillis) / 1000.0F, 0.05F);
         this.timeMillis = timeMillis;
         if (this.speedSnapFlag) {
            this.growProgress = Math.min(1.0F, this.growProgress + growFactor * 4.0F);
         } else {
            this.growProgress = Math.max(0.0F, this.growProgress - growFactor * 5.0F);
            if (this.growProgress <= 0.01F) {
               this.growProgress = 0.0F;
               this.growSlideFlag = false;
               return;
            }
         }

         this.speedSnapAmount += growFactor * 0.1F;
         if (this.speedSnapAmount > 1.0F) {
            this.speedSnapAmount--;
         }

         float growFactor2 = (float)this.doubleHelper.getValue();
         float growFactor3 = (float)(1.0 - Math.pow(0.001, growFactor * growFactor2 * 0.1));
         this.animatedMinX = this.animatedMinX + (this.minX - this.animatedMinX) * growFactor3;
         this.blockHighlightAmount = this.blockHighlightAmount + (this.minY - this.blockHighlightAmount) * growFactor3;
         this.growAmount3Grow = this.growAmount3Grow + (this.minZ - this.growAmount3Grow) * growFactor3;
         this.growAmountGrow = this.growAmountGrow + (this.maxX - this.growAmountGrow) * growFactor3;
         this.growAmount2Grow = this.growAmount2Grow + (this.maxY - this.growAmount2Grow) * growFactor3;
         this.growAmount = this.growAmount + (this.maxZ - this.growAmount) * growFactor3;
         float growFactor4 = getAmount(this.growProgress);
         double growValue;
         double growValue2;
         double growValue3;
         double growValue4;
         double growValue5;
         double growValue6;
         if (this.enumHelper.getCurrentOption().equals("Grow")) {
            double growValue7 = (this.animatedMinX + this.growAmountGrow) / 2.0;
            double growValue8 = (this.blockHighlightAmount + this.growAmount2Grow) / 2.0;
            double growValue9 = (this.growAmount3Grow + this.growAmount) / 2.0;
            double growValue10 = (this.growAmountGrow - this.animatedMinX) / 2.0 * growFactor4;
            double growValue11 = (this.growAmount2Grow - this.blockHighlightAmount) / 2.0 * growFactor4;
            double growValue12 = (this.growAmount - this.growAmount3Grow) / 2.0 * growFactor4;
            growValue = growValue7 - growValue10 - 0.006F;
            growValue2 = growValue8 - growValue11 - 0.006F;
            growValue3 = growValue9 - growValue12 - 0.006F;
            growValue4 = growValue7 + growValue10 + 0.006F;
            growValue5 = growValue8 + growValue11 + 0.006F;
            growValue6 = growValue9 + growValue12 + 0.006F;
         } else {
            growValue = this.animatedMinX - 0.006F;
            growValue2 = this.blockHighlightAmount - 0.006F;
            growValue3 = this.growAmount3Grow - 0.006F;
            growValue4 = this.growAmountGrow + 0.006F;
            growValue5 = this.growAmount2Grow + 0.006F;
            growValue6 = this.growAmount + 0.006F;
         }

         float growLimit = this.enumHelper.getCurrentOption().equals("Grow") ? growFactor4 : Math.min(1.0F, this.growProgress * 2.0F);
         int growValue13;
         int growValue14;
         if (this.booleanBase.isEnabled()) {
            int blockHighlightValue = MatrixEspUtils.releaseRightValue & 16777215;
            growValue13 = getValue(blockHighlightValue, (int)(200.0F * growLimit));
            growValue14 = getValue(blockHighlightValue, (int)(40.0F * growLimit));
         } else {
            growValue13 = MatrixAmountUtils.getValue(this.outlineGrow.getColor(), growLimit);
            growValue14 = MatrixAmountUtils.getValue(this.colorBase.getColor(), growLimit);
         }

         Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
         MatrixStack matrices = queueCommand.matrices;
         matrices.push();
         matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
         Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
         double growLimit2 = this.maxX - this.minX;
         double growLimit3 = this.maxY - this.minY;
         double growLimit4 = this.maxZ - this.minZ;
         double growValue16 = (growValue + growValue4) / 2.0;
         double growValue17 = (growValue2 + growValue5) / 2.0;
         double growValue18 = (growValue3 + growValue6) / 2.0;
         double growFactor5 = growLimit2 > 0.0 ? (growValue4 - growValue) / growLimit2 : 1.0;
         double growFactor6 = growLimit3 > 0.0 ? (growValue5 - growValue2) / growLimit3 : 1.0;
         double growFactor7 = growLimit4 > 0.0 ? (growValue6 - growValue3) / growLimit4 : 1.0;
         int matrixValue = growValue14;
         int n2 = growValue13;
         this.outlineShape
            .forEachBox(
               (oocArgb, aMul, scaleZ, maxY, minX, focArgb) -> {
                  double growValue21 = oocArgb + this.blockPos.getX();
                  double growValue22 = aMul + this.blockPos.getY();
                  double var33x = scaleZ + this.blockPos.getZ();
                  double var35x = maxY + this.blockPos.getX();
                  double var37x = minX + this.blockPos.getY();
                  double var39x = focArgb + this.blockPos.getZ();
                  double var41x = (growValue21 + var35x) / 2.0;
                  double var43x = (growValue22 + var37x) / 2.0;
                  double var45x = (var33x + var39x) / 2.0;
                  double var47x = (var41x - (this.minX + this.maxX) / 2.0) * growFactor5;
                  double var49x = (var43x - (this.minY + this.maxY) / 2.0) * growFactor6;
                  double var51x = (var45x - (this.minZ + this.maxZ) / 2.0) * growFactor7;
                  double var53x = (var35x - growValue21) * growFactor5;
                  double var55x = (var37x - growValue22) * growFactor6;
                  double var57x = (var39x - var33x) * growFactor7;
                  double snapSpeedValue = growValue16 + var47x - var53x / 2.0;
                  double growValue24 = growValue17 + var49x - var55x / 2.0;
                  double growValue25 = growValue18 + var51x - var57x / 2.0;
                  double growValue26 = growValue16 + var47x + var53x / 2.0;
                  double growValue27 = growValue17 + var49x + var55x / 2.0;
                  double growValue28 = growValue18 + var51x + var57x / 2.0;
                  VertexBuilderUtils.handle(
                     positionMatrix,
                     (float)snapSpeedValue,
                     (float)growValue24,
                     (float)growValue25,
                     (float)growValue26,
                     (float)growValue27,
                     (float)growValue28,
                     MatrixAmountUtils.getAmount(matrixValue),
                     MatrixAmountUtils.getAmount4(matrixValue),
                     MatrixAmountUtils.getAmount6(matrixValue),
                     MatrixAmountUtils.getAmountFloat(matrixValue)
                  );
                  if (growLimit > 0.01F) {
                     this.handleSnapSpeed(positionMatrix, snapSpeedValue, growValue24, growValue25, growValue26, growValue27, growValue28, growLimit);
                  }

                  VertexBuilderUtils.handle(positionMatrix, (float)snapSpeedValue, (float)growValue24, (float)growValue25, (float)growValue26, (float)growValue27, (float)growValue28, n2, 1.5F);
               }
            );
         matrices.pop();
         VertexBuilderUtils.updateDrawImmediate();
      }
   }

   private void handleSnapSpeed(Matrix4f matrix, double snapSpeedValue, double snapSpeedValue2, double snapSpeedValue3, double snapSpeedValue4, double snapSpeedValue5, double snapSpeedValue6, float growLimit) {
      VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();
      float y12 = (float)snapSpeedValue;
      float z12 = (float)snapSpeedValue2;
      float snapSpeedValue8 = (float)snapSpeedValue3;
      float x22 = (float)snapSpeedValue4;
      float z22 = (float)snapSpeedValue5;
      float x12 = (float)snapSpeedValue6;
      float deltaVar = x22 - y12;
      if (!(deltaVar < 0.001F)) {
         float speedSnapAmountScaled = this.speedSnapAmount * 2.0F;
         float snapSpeedFactor = speedSnapAmountScaled <= 1.0F ? getAmountForFloat(speedSnapAmountScaled) : getAmountForFloat(2.0F - speedSnapAmountScaled);
         float vertexColorFactor = 0.095F;
         float snapSpeedValue9 = 0.45F * growLimit;
         float vertexColorValue = y12 - vertexColorFactor * deltaVar + (deltaVar + 0.19F * deltaVar) * snapSpeedFactor;
         this.handleVertexColor(
            buffer, matrix, y12, deltaVar, vertexColorValue, vertexColorFactor, snapSpeedValue9, y12, z12, snapSpeedValue8, y12, z22, snapSpeedValue8, x22, z22, snapSpeedValue8, x22, z12, snapSpeedValue8
         );
         this.handleVertexColor(
            buffer, matrix, y12, deltaVar, vertexColorValue, vertexColorFactor, snapSpeedValue9, y12, z12, x12, x22, z12, x12, x22, z22, x12, y12, z22, x12
         );
         this.handleVertexColorForVertexConsumerMatrix4f(
            buffer, matrix, vertexColorValue, y12, vertexColorFactor, deltaVar, snapSpeedValue9, y12, z12, snapSpeedValue8, y12, z12, x12, y12, z22, x12, y12, z22, snapSpeedValue8
         );
         this.handleVertexColorForVertexConsumerMatrix4f(
            buffer, matrix, vertexColorValue, x22, vertexColorFactor, deltaVar, snapSpeedValue9, x22, z12, snapSpeedValue8, x22, z22, snapSpeedValue8, x22, z22, x12, x22, z12, x12
         );
         this.handleVertexColor(
            buffer, matrix, y12, deltaVar, vertexColorValue, vertexColorFactor, snapSpeedValue9, y12, z22, snapSpeedValue8, y12, z22, x12, x22, z22, x12, x22, z22, snapSpeedValue8
         );
         this.handleVertexColor(
            buffer, matrix, y12, deltaVar, vertexColorValue, vertexColorFactor, snapSpeedValue9, y12, z12, snapSpeedValue8, x22, z12, snapSpeedValue8, x22, z12, x12, y12, z12, x12
         );
      }
   }

   private static int getValue(int blockHighlightValue, int blockHighlightBound) {
      return Math.max(1, Math.min(255, blockHighlightBound)) << 24 | blockHighlightValue & 16777215;
   }

   private static float getAmount(float blockHighlightValue) {
      float blockHighlightFactor = 1.70158F;
      float blockHighlightFactor2 = blockHighlightFactor + 1.0F;
      return 1.0F + blockHighlightFactor2 * (float)Math.pow(blockHighlightValue - 1.0F, 3.0) + blockHighlightFactor * (float)Math.pow(blockHighlightValue - 1.0F, 2.0);
   }

   private void handleGrowMin(Box box) {
      this.minX = box.minX;
      this.minY = box.minY;
      this.minZ = box.minZ;
      this.maxX = box.maxX;
      this.maxY = box.maxY;
      this.maxZ = box.maxZ;
      if (!this.growSlideFlag) {
         String formatText = this.enumHelper.getCurrentOption();
         switch (formatText) {
            case "Grow":
               double growAmount = (this.minX + this.maxX) / 2.0;
               double growAmount2 = (this.minY + this.maxY) / 2.0;
               double growAmount3 = (this.minZ + this.maxZ) / 2.0;
               this.animatedMinX = growAmount;
               this.blockHighlightAmount = growAmount2;
               this.growAmount3Grow = growAmount3;
               this.growAmountGrow = growAmount;
               this.growAmount2Grow = growAmount2;
               this.growAmount = growAmount3;
               break;
            case "Slide":
               this.animatedMinX = this.minX;
               this.blockHighlightAmount = this.minY + 1.0;
               this.growAmount3Grow = this.minZ;
               this.growAmountGrow = this.maxX;
               this.growAmount2Grow = this.maxY + 1.0;
               this.growAmount = this.maxZ;
               break;
            default:
               this.animatedMinX = this.minX;
               this.blockHighlightAmount = this.minY;
               this.growAmount3Grow = this.minZ;
               this.growAmountGrow = this.maxX;
               this.growAmount2Grow = this.maxY;
               this.growAmount = this.maxZ;
         }

         this.growProgress = 0.0F;
         this.speedSnapAmount = 0.0F;
         this.growSlideFlag = true;
      }
   }

   @Override
   public void onDisable() {
      this.growSlideFlag = false;
      this.speedSnapFlag = false;
      this.blockPos = null;
      this.growProgress = 0.0F;
      this.speedSnapAmount = 0.0F;
   }

   public BlockHighlight() {
      super("BlockHighlight", IconsPngType.VISUAL);
      this.colorBase = new ColorSetting("Fill", "Fill colour (when Client Color is off)", 687865855);
      this.enumHelper = new EnumSetting("Mode", "Appear animation", "Default", "Default", "Grow", "Slide");
      this.doubleHelper = new DoubleSetting("Speed", "Follow / snap speed", 8.0, 1.0, 67.0, 0.5);
      this.growSlideFlag = false;
      this.speedSnapFlag = false;
      this.growProgress = 0.0F;
      this.blockPos = null;
      this.timeMillis = System.currentTimeMillis();
      this.speedSnapAmount = 0.0F;
      this.addSettings(new ConfigSetting[]{this.booleanBase, this.outlineGrow, this.colorBase, this.enumHelper, this.doubleHelper});
   }

   private static float getAmountForFloat(float blockHighlightFactor) {
      return blockHighlightFactor < 0.5F ? 4.0F * blockHighlightFactor * blockHighlightFactor * blockHighlightFactor : 1.0F - (float)Math.pow(-2.0F * blockHighlightFactor + 2.0F, 3.0) / 2.0F;
   }

   private void handleVertexColorForVertexConsumerMatrix4f(
      VertexConsumer vertices,
      Matrix4f matrix,
      float vertexColorValue,
      float vertexColorValue2,
      float vertexColorFactor,
      float deltaVar,
      float vertexColorFactor3,
      float vertexColorValue3,
      float vertexColorValue4,
      float vertexColorValue5,
      float vertexColorValue6,
      float z12,
      float vertexColorValue8,
      float y12,
      float vertexColorValue10,
      float x12,
      float vertexColorValue12,
      float z22,
      float vertexColorValue14
   ) {
      float vertexColorLimit = Math.abs(vertexColorValue2 - vertexColorValue);
      float vertexColorDivisor = vertexColorFactor * deltaVar;
      if (!(vertexColorLimit >= vertexColorDivisor)) {
         float vertexColorFactor4 = 1.0F - vertexColorLimit / vertexColorDivisor;
         float vertexColorValue15 = vertexColorFactor4 * vertexColorFactor4 * (3.0F - 2.0F * vertexColorFactor4) * 0.5F;
         float vertexColorValue16 = vertexColorFactor4 * vertexColorFactor4 * vertexColorFactor4 * vertexColorFactor4 * 0.5F;
         int max17 = Math.max(0, Math.min(255, (int)((vertexColorValue15 + vertexColorValue16) * vertexColorFactor3 * 255.0F)));
         if (max17 != 0) {
            vertices.vertex(matrix, vertexColorValue3, vertexColorValue4, vertexColorValue5).color(255, 253, 248, max17);
            vertices.vertex(matrix, vertexColorValue6, z12, vertexColorValue8).color(255, 253, 248, max17);
            vertices.vertex(matrix, y12, vertexColorValue10, x12).color(255, 253, 248, max17);
            vertices.vertex(matrix, vertexColorValue12, z22, vertexColorValue14).color(255, 253, 248, max17);
         }
      }
   }

   private int getValue(float blockHighlightValue, float blockHighlightFactor, float blockHighlightValue2, float vertexColorFactor, float blockHighlightFactor3) {
      float blockHighlightLimit = Math.abs(blockHighlightValue - blockHighlightValue2);
      float blockHighlightDivisor = vertexColorFactor * blockHighlightFactor;
      if (blockHighlightLimit >= blockHighlightDivisor) {
         return 0;
      } else {
         float blockHighlightFactor4 = 1.0F - blockHighlightLimit / blockHighlightDivisor;
         float blockHighlightValue3 = blockHighlightFactor4 * blockHighlightFactor4 * (3.0F - 2.0F * blockHighlightFactor4) * 0.5F;
         float blockHighlightValue4 = blockHighlightFactor4 * blockHighlightFactor4 * blockHighlightFactor4 * blockHighlightFactor4 * 0.5F;
         return Math.max(0, Math.min(255, (int)((blockHighlightValue3 + blockHighlightValue4) * blockHighlightFactor3 * 255.0F)));
      }
   }
}
