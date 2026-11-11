package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import com.corz.client.categories.movement.Freecam;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class BlockOverlay extends Module {
   private final DoubleSetting doubleHelper;
   private final ColorSetting colorBase;
   private final DoubleSetting fillAlpha;
   private final DoubleSetting lineWidth;
   private final DoubleSetting outlineAlpha;
   private final EnumSetting enumHelper = new EnumSetting("Mode", "Overlay render style", "Fill + Outline", "Outline", "Fill", "Fill + Outline", "Glow", "Shader");
   private final EnumSetting colorRainbowGradient = new EnumSetting("Color", "Colour source", "Theme", "Static", "Gradient", "Rainbow", "Theme");
   private final ColorSetting secondaryColor;
   private final DoubleSetting glowStrength;

   private int getRainbowGradient() {
      float rainbowGradientValue = (float)this.doubleHelper.getValue();
      String formatText = this.colorRainbowGradient.getCurrentOption();

      return switch (formatText) {
         case "Static" -> this.colorBase.getColor();
         case "Gradient" -> MatrixAmountUtils.getValue(this.colorBase.getColor(), this.secondaryColor.getColor(), rainbowGradientValue, 0.0F);
         case "Rainbow" -> MatrixAmountUtils.getValue(rainbowGradientValue, 0.0F);
         default -> MatrixEspUtils.releaseRightValue;
      };
   }

   private static float[] getCameraMin(Box box) {
      Vec3d cameraPos = mc.getEntityRenderDispatcher().camera.getCameraPos();
      return new float[]{
         (float)(box.minX - cameraPos.getX()),
         (float)(box.minY - cameraPos.getY()),
         (float)(box.minZ - cameraPos.getZ()),
         (float)(box.maxX - cameraPos.getX()),
         (float)(box.maxY - cameraPos.getY()),
         (float)(box.maxZ - cameraPos.getZ())
      };
   }

   public static void handlePeekPosition(MatrixStack matrices, Box box, int peekPositionValue, double peekPositionValue2) {
      float[] cameraMin = getCameraMin(box);
      VertexBuilderUtils.handle(matrices.peek().getPositionMatrix(), cameraMin[0], cameraMin[1], cameraMin[2], cameraMin[3], cameraMin[4], cameraMin[5], peekPositionValue, (float)peekPositionValue2);
   }

   public static void handlePeekPosition(MatrixStack matrices, Box box, int peekPositionValue) {
      float[] cameraMin = getCameraMin(box);
      VertexBuilderUtils.handle(
         matrices.peek().getPositionMatrix(),
         cameraMin[0],
         cameraMin[1],
         cameraMin[2],
         cameraMin[3],
         cameraMin[4],
         cameraMin[5],
         MatrixAmountUtils.getAmount(peekPositionValue),
         MatrixAmountUtils.getAmount4(peekPositionValue),
         MatrixAmountUtils.getAmount6(peekPositionValue),
         MatrixAmountUtils.getAmountFloat(peekPositionValue)
      );
   }

   private void handle(Matrix4f matrix, float[] blockOverlayValues, int blockOverlayValue, float blockOverlayValue2, boolean blockOverlayFlag) {
      if (blockOverlayFlag) {
         this.handle(matrix, blockOverlayValues, blockOverlayValue2, MatrixAmountUtils.getAmountFloat(blockOverlayValue));
      } else {
         VertexBuilderUtils.handle(matrix, blockOverlayValues[0], blockOverlayValues[1], blockOverlayValues[2], blockOverlayValues[3], blockOverlayValues[4], blockOverlayValues[5], blockOverlayValue, blockOverlayValue2);
      }
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      if (!Freecam.isInstanceFlag() && !ClampMatrixUtils.isFrameCamera()) {
         if (mc.crosshairTarget instanceof BlockHitResult blockHitResult && mc.world != null) {
            VoxelShape outlineShape = mc.world.getBlockState(blockHitResult.getBlockPos()).getOutlineShape(mc.world, blockHitResult.getBlockPos());
            if (!outlineShape.isEmpty()) {
               Box offset = outlineShape.getBoundingBox().offset(blockHitResult.getBlockPos());
               float[] cameraMin = getCameraMin(offset);
               Matrix4f positionMatrix = queueCommand.matrices.peek().getPositionMatrix();
               String formatText = this.enumHelper.getCurrentOption();
               boolean blockOverlayFlag = formatText.equals("Fill") || formatText.equals("Fill + Outline") || formatText.equals("Glow") || formatText.equals("Shader");
               boolean blockOverlayFlag2 = !formatText.equals("Fill");
               boolean equals = formatText.equals("Shader");
               boolean blockOverlayFlag3 = formatText.equals("Glow") || equals;
               int rainbowGradient = this.getRainbowGradient();
               if (blockOverlayFlag) {
                  int blockOverlayValue = MatrixAmountUtils.getValueInt(rainbowGradient, (float)this.fillAlpha.getValue());
                  VertexBuilderUtils.handle(
                     positionMatrix,
                     cameraMin[0],
                     cameraMin[1],
                     cameraMin[2],
                     cameraMin[3],
                     cameraMin[4],
                     cameraMin[5],
                     MatrixAmountUtils.getAmount(blockOverlayValue),
                     MatrixAmountUtils.getAmount4(blockOverlayValue),
                     MatrixAmountUtils.getAmount6(blockOverlayValue),
                     MatrixAmountUtils.getAmountFloat(blockOverlayValue)
                  );
               }

               if (blockOverlayFlag2) {
                  float blockOverlayValue2 = (float)this.lineWidth.getValue();
                  float blockOverlayFactor = (float)this.outlineAlpha.getValue();
                  if (blockOverlayFlag3 && this.glowStrength.getValue() > 0.001) {
                     float blockOverlayValue3 = (float)this.glowStrength.getValue();
                     this.handle(positionMatrix, cameraMin, MatrixAmountUtils.getValueInt(rainbowGradient, 0.1F * blockOverlayValue3 * blockOverlayFactor), blockOverlayValue2 * 3.2F, equals);
                     this.handle(positionMatrix, cameraMin, MatrixAmountUtils.getValueInt(rainbowGradient, 0.2F * blockOverlayValue3 * blockOverlayFactor), blockOverlayValue2 * 1.9F, equals);
                  }

                  if (equals) {
                     this.handle(positionMatrix, cameraMin, blockOverlayValue2, blockOverlayFactor);
                  } else {
                     VertexBuilderUtils.handle(positionMatrix, cameraMin[0], cameraMin[1], cameraMin[2], cameraMin[3], cameraMin[4], cameraMin[5], MatrixAmountUtils.getValueInt(rainbowGradient, blockOverlayFactor), blockOverlayValue2);
                  }
               }

               VertexBuilderUtils.updateDrawImmediate();
            }
         }
      }
   }

   @Override
   public void onDisable() {
   }

   private void handle(Matrix4f matrix, float[] blockOverlayValues, float blockOverlayValue, float matrixLimit) {
      float matrixValue = (float)this.doubleHelper.getValue();
      float blockOverlayValue4 = blockOverlayValues[0];
      float blockOverlayValue5 = blockOverlayValues[1];
      float blockOverlayValue6 = blockOverlayValues[2];
      float blockOverlayValue7 = blockOverlayValues[3];
      float blockOverlayValue8 = blockOverlayValues[4];
      float blockOverlayValue9 = blockOverlayValues[5];
      float[][] floats = new float[][]{
         {blockOverlayValue4, blockOverlayValue5, blockOverlayValue6, blockOverlayValue7, blockOverlayValue5, blockOverlayValue6},
         {blockOverlayValue7, blockOverlayValue5, blockOverlayValue6, blockOverlayValue7, blockOverlayValue5, blockOverlayValue9},
         {blockOverlayValue7, blockOverlayValue5, blockOverlayValue9, blockOverlayValue4, blockOverlayValue5, blockOverlayValue9},
         {blockOverlayValue4, blockOverlayValue5, blockOverlayValue9, blockOverlayValue4, blockOverlayValue5, blockOverlayValue6},
         {blockOverlayValue4, blockOverlayValue8, blockOverlayValue6, blockOverlayValue7, blockOverlayValue8, blockOverlayValue6},
         {blockOverlayValue7, blockOverlayValue8, blockOverlayValue6, blockOverlayValue7, blockOverlayValue8, blockOverlayValue9},
         {blockOverlayValue7, blockOverlayValue8, blockOverlayValue9, blockOverlayValue4, blockOverlayValue8, blockOverlayValue9},
         {blockOverlayValue4, blockOverlayValue8, blockOverlayValue9, blockOverlayValue4, blockOverlayValue8, blockOverlayValue6},
         {blockOverlayValue4, blockOverlayValue5, blockOverlayValue6, blockOverlayValue4, blockOverlayValue8, blockOverlayValue6},
         {blockOverlayValue7, blockOverlayValue5, blockOverlayValue6, blockOverlayValue7, blockOverlayValue8, blockOverlayValue6},
         {blockOverlayValue7, blockOverlayValue5, blockOverlayValue9, blockOverlayValue7, blockOverlayValue8, blockOverlayValue9},
         {blockOverlayValue4, blockOverlayValue5, blockOverlayValue9, blockOverlayValue4, blockOverlayValue8, blockOverlayValue9}
      };

      for (int i = 0; i < floats.length; i++) {
         int valueInt = MatrixAmountUtils.getValueInt(MatrixAmountUtils.getValue(matrixValue, i * 0.06F), matrixLimit);
         int valueInt11 = MatrixAmountUtils.getValueInt(MatrixAmountUtils.getValue(matrixValue, (i + 1) * 0.06F), matrixLimit);
         VertexBuilderUtils.handle(matrix, floats[i][0], floats[i][1], floats[i][2], floats[i][3], floats[i][4], floats[i][5], valueInt, valueInt11, blockOverlayValue);
      }
   }

   public BlockOverlay() {
      super("Block Overlay", IconsPngType.VISUAL);
      this.colorBase = new ColorSetting("Primary Color", "Main colour (Static / Gradient)", -11884289);
      this.secondaryColor = new ColorSetting("Secondary Color", "Second colour (Gradient)", -4961537);
      this.fillAlpha = new DoubleSetting("Fill Alpha", "Translucent fill opacity", 0.22, 0.0, 1.0, 0.02);
      this.outlineAlpha = new DoubleSetting("Outline Alpha", "Outline opacity", 0.95, 0.0, 1.0, 0.02);
      this.lineWidth = new DoubleSetting("Line Width", "Outline thickness (px)", 2.0, 0.5, 5.0, 0.5);
      this.glowStrength = new DoubleSetting("Glow Strength", "Soft edge bloom (Glow / Shader)", 0.5, 0.0, 1.0, 0.05);
      this.doubleHelper = new DoubleSetting("Animation Speed", "Gradient / rainbow speed", 1.0, 0.0, 5.0, 0.1);
      this.addSettings(
         new ConfigSetting[]{
            this.enumHelper,
            this.colorRainbowGradient,
            this.colorBase,
            this.secondaryColor,
            this.fillAlpha,
            this.outlineAlpha,
            this.lineWidth,
            this.glowStrength,
            this.doubleHelper
         }
      );
   }

   @Override
   public void onEnable() {
   }
}
