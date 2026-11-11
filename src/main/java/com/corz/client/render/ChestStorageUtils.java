package com.corz.client.render;

import com.corz.client.CorzClient;
import com.corz.client.world.RoleDimension;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;

public final class ChestStorageUtils {
   private static final List<float[]> OCCUPIED_RECTS = new ArrayList<>();

   private static float getAmount(float chestStorageValue, float chestStorageValue2, float chestStorageValue3, float chestStorageValue4) {
      for (int i = 0; i < 6; i++) {
         boolean chestStorageFlag = false;

         for (float[] chestStorageValues : OCCUPIED_RECTS) {
            if (chestStorageValue < chestStorageValues[2] && chestStorageValue + chestStorageValue3 > chestStorageValues[0] && chestStorageValue2 < chestStorageValues[3] && chestStorageValue2 + chestStorageValue4 > chestStorageValues[1]) {
               chestStorageValue2 = chestStorageValues[1] - chestStorageValue4 - 2.0F >= 2.0F ? chestStorageValues[1] - chestStorageValue4 - 2.0F : chestStorageValues[3] + 2.0F;
               chestStorageFlag = true;
            }
         }

         if (!chestStorageFlag) {
            break;
         }
      }

      handle(chestStorageValue, chestStorageValue2, chestStorageValue3, chestStorageValue4);
      return chestStorageValue2;
   }

   public static float getAmount() {
      double chestStorageFactor = System.currentTimeMillis() % 1200L / 1200.0;
      return 0.5F + 0.5F * (float)Math.sin(chestStorageFactor * Math.PI * 2.0);
   }

   public static void handleTranslatePush(MatrixStack matrices, String translatePushText, String corzfontText, int translatePushValue, float x, float y, float x2) {
      VertexTextureFont vertexTexture = CorzClient.fontManager.getLargeFont();
      float translatePushFactor = vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown();
      float translatePushValue2 = translatePushFactor * 0.42F;
      float translatePushValue3 = 4.0F;
      float translatePushValue4 = 6.0F;
      float translatePushValue5 = 4.0F;
      float translatePushValue6 = 1.5F;
      boolean translatePushFlag = corzfontText != null && !corzfontText.isEmpty();
      float max7 = Math.max(vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(translatePushText), translatePushFlag ? vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) : 0.0F);
      float translatePushFactor2 = translatePushValue2 + translatePushValue3 + max7 + translatePushValue4 * 2.0F;
      float translatePushFactor3 = translatePushFactor * (translatePushFlag ? 2 : 1) + (translatePushFlag ? translatePushValue6 : 0.0F) + translatePushValue5 * 2.0F;
      y = getAmount(x - translatePushFactor2 * x2 / 2.0F, y - translatePushFactor3 * x2, translatePushFactor2 * x2, translatePushFactor3 * x2) + translatePushFactor3 * x2;
      matrices.push();
      matrices.translate(x, y, 0.0F);
      matrices.scale(x2, x2, 1.0F);
      matrices.translate(-x, -y, 0.0F);
      float vertexConsumerValue = x - translatePushFactor2 / 2.0F;
      float f12 = y - translatePushFactor3;
      int translatePushValue10 = -16184564;
      VertexConsumerUtilsVector.handle(
         matrices, new Color(translatePushValue10 >> 16 & 0xFF, translatePushValue10 >> 8 & 0xFF, translatePushValue10 & 0xFF, 215), vertexConsumerValue, f12, vertexConsumerValue + translatePushFactor2, f12 + translatePushFactor3, 2.5, 4.0
      );
      float translatePushValue11 = vertexConsumerValue + translatePushValue4;
      float translatePushValue12 = f12 + translatePushValue5 + (translatePushFactor - translatePushValue2) / 2.0F;
      VertexConsumerUtilsVector.handle(
         matrices, new Color(translatePushValue >> 16 & 0xFF, translatePushValue >> 8 & 0xFF, translatePushValue & 0xFF, 255), translatePushValue11, translatePushValue12, translatePushValue11 + translatePushValue2, translatePushValue12 + translatePushValue2, translatePushValue2 / 2.0F, 6.0
      );
      float vertexTextureValue = vertexConsumerValue + translatePushValue4 + translatePushValue2 + translatePushValue3;
      vertexTexture.handleForStringFloat(translatePushText, vertexTextureValue, f12 + translatePushValue5, new Color(-1));
      if (translatePushFlag) {
         vertexTexture.handleForStringFloat(corzfontText, vertexTextureValue, f12 + translatePushValue5 + translatePushFactor + translatePushValue6, new Color(-8421505));
      }

      matrices.pop();
   }

   public static void handle(Matrix4f matrix, BlockPos pos, int chestStorageValue, float chestStorageFactor, boolean chestStorageFlag) {
      float x = pos.getX();
      float z = pos.getZ();
      float vertexBuilderValue = x + 1.0F;
      float chestStorageValue3 = z + 1.0F;
      float chestStorageValue4 = pos.getY() + 1.01F;
      VertexBuilderUtils.handle(matrix, x, chestStorageValue4, z, vertexBuilderValue, chestStorageValue4, z, vertexBuilderValue, chestStorageValue4, chestStorageValue3, x, chestStorageValue4, chestStorageValue3, getValue(chestStorageValue, chestStorageFactor * (chestStorageFlag ? 0.26F : 0.18F)));
      int playerValue = getValue(chestStorageValue, chestStorageFactor * 0.55F);
      VertexBuilderUtils.handleForMatrix4fFloat(matrix, x, chestStorageValue4, z, vertexBuilderValue, chestStorageValue4, z, playerValue, 1.2F);
      VertexBuilderUtils.handleForMatrix4fFloat(matrix, vertexBuilderValue, chestStorageValue4, z, vertexBuilderValue, chestStorageValue4, chestStorageValue3, playerValue, 1.2F);
      VertexBuilderUtils.handleForMatrix4fFloat(matrix, vertexBuilderValue, chestStorageValue4, chestStorageValue3, x, chestStorageValue4, chestStorageValue3, playerValue, 1.2F);
      VertexBuilderUtils.handleForMatrix4fFloat(matrix, x, chestStorageValue4, chestStorageValue3, x, chestStorageValue4, z, playerValue, 1.2F);
      float vertexColorValue = x + 0.5F;
      float amountForDouble = z + 0.5F;
      int chestStorageValue8 = getValue(chestStorageValue, chestStorageFactor * (chestStorageFlag ? 0.6F : 0.35F));
      int valueInt = getValue(chestStorageValue, 0.0F);
      VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();
      VertexBuilderUtils.handleVertexColor(
         buffer,
         matrix,
         vertexColorValue - 0.16F,
         chestStorageValue4,
         amountForDouble,
         chestStorageValue8,
         vertexColorValue + 0.16F,
         chestStorageValue4,
         amountForDouble,
         chestStorageValue8,
         vertexColorValue + 0.16F,
         chestStorageValue4 + 2.5F,
         amountForDouble,
         valueInt,
         vertexColorValue - 0.16F,
         chestStorageValue4 + 2.5F,
         amountForDouble,
         valueInt
      );
      VertexBuilderUtils.handleVertexColor(
         buffer,
         matrix,
         vertexColorValue,
         chestStorageValue4,
         amountForDouble - 0.16F,
         chestStorageValue8,
         vertexColorValue,
         chestStorageValue4,
         amountForDouble + 0.16F,
         chestStorageValue8,
         vertexColorValue,
         chestStorageValue4 + 2.5F,
         amountForDouble + 0.16F,
         valueInt,
         vertexColorValue,
         chestStorageValue4 + 2.5F,
         amountForDouble - 0.16F,
         valueInt
      );
      double angle = System.currentTimeMillis() % 3000L / 3000.0 * Math.PI * 2.0;
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float chestStorageFactor2 = 0.56F;
      float deltaY = chestStorageValue4 + 0.01F;
      int chestStorageValue11 = getValue(chestStorageValue, chestStorageFactor * 0.95F);

      for (int i = 0; i < 4; i++) {
         float chestStorageFactor3 = i != 0 && i != 3 ? 1.0F : -1.0F;
         float chestStorageFactor4 = i < 2 ? -1.0F : 1.0F;
         float chestStorageValue12 = vertexColorValue + (chestStorageFactor3 * chestStorageFactor2 * cos - chestStorageFactor4 * chestStorageFactor2 * sin);
         float chestStorageValue13 = amountForDouble + (chestStorageFactor3 * chestStorageFactor2 * sin + chestStorageFactor4 * chestStorageFactor2 * cos);
         float chestStorageFactor5 = -chestStorageFactor3 * 0.2F;
         float chestStorageFactor6 = 0.0F;
         float chestStorageFactor7 = 0.0F;
         float chestStorageFactor8 = -chestStorageFactor4 * 0.2F;
         VertexBuilderUtils.handleForMatrix4fFloat(
            matrix, chestStorageValue12, deltaY, chestStorageValue13, chestStorageValue12 + (chestStorageFactor5 * cos - chestStorageFactor6 * sin), deltaY, chestStorageValue13 + (chestStorageFactor5 * sin + chestStorageFactor6 * cos), chestStorageValue11, 2.0F
         );
         VertexBuilderUtils.handleForMatrix4fFloat(
            matrix, chestStorageValue12, deltaY, chestStorageValue13, chestStorageValue12 + (chestStorageFactor7 * cos - chestStorageFactor8 * sin), deltaY, chestStorageValue13 + (chestStorageFactor7 * sin + chestStorageFactor8 * cos), chestStorageValue11, 2.0F
         );
      }
   }

   public static float getAmount(double chestStorageLimit) {
      if (chestStorageLimit > 48.0) {
         return 0.0F;
      } else {
         return chestStorageLimit > 24.0 ? 0.6F : 1.0F;
      }
   }

   public static void handle(float chestStorageValue, float chestStorageValue2, float chestStorageValue3, float chestStorageValue4) {
      OCCUPIED_RECTS.add(new float[]{chestStorageValue, chestStorageValue2, chestStorageValue + chestStorageValue3, chestStorageValue2 + chestStorageValue4});
   }

   public static void handleScaledWindow(MatrixStack matrices, String scaledWindowText, String corzfontText, int scaledWindowValue) {
      MinecraftClient instance = MinecraftClient.getInstance();
      VertexTextureFont vertexTexture = CorzClient.fontManager.getLargeFont();
      float abcdefghijklmnopqrstuvwxyzBrown = vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown();
      float scaledWindowValue3 = abcdefghijklmnopqrstuvwxyzBrown * 0.42F;
      float scaledWindowValue4 = 5.0F;
      float scaledWindowValue5 = 8.0F;
      float scaledWindowValue6 = 5.0F;
      float scaledWindowValue7 = 8.0F;
      float scaledWindowValue8 = vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(scaledWindowText);
      float scaledWindowValue9 = corzfontText == null ? 0.0F : vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText);
      float scaledWindowValue10 = scaledWindowValue5 * 2.0F + scaledWindowValue3 + scaledWindowValue4 + scaledWindowValue8 + (corzfontText == null ? 0.0F : scaledWindowValue7 + scaledWindowValue9);
      float scaledWindowValue11 = abcdefghijklmnopqrstuvwxyzBrown + scaledWindowValue6 * 2.0F;
      float vertexConsumerValue = instance.getWindow().getScaledWidth() / 2.0F - scaledWindowValue10 / 2.0F;
      float scaledWindowValue13 = instance.getWindow().getScaledHeight() / 2.0F + 26.0F;
      int scaledWindowValue14 = -16184564;
      VertexConsumerUtilsVector.handle(
         matrices, new Color(scaledWindowValue14 >> 16 & 0xFF, scaledWindowValue14 >> 8 & 0xFF, scaledWindowValue14 & 0xFF, 225), vertexConsumerValue, scaledWindowValue13, vertexConsumerValue + scaledWindowValue10, scaledWindowValue13 + scaledWindowValue11, 4.0, 6.0
      );
      float scaledWindowValue15 = 0.6F + 0.4F * getAmountFloat();
      float scaledWindowValue16 = vertexConsumerValue + scaledWindowValue5;
      float f12 = scaledWindowValue13 + scaledWindowValue6 + (abcdefghijklmnopqrstuvwxyzBrown - scaledWindowValue3) / 2.0F;
      VertexConsumerUtilsVector.handle(
         matrices,
         new Color(scaledWindowValue >> 16 & 0xFF, scaledWindowValue >> 8 & 0xFF, scaledWindowValue & 0xFF, Math.round(255.0F * scaledWindowValue15)),
         scaledWindowValue16,
         f12,
         scaledWindowValue16 + scaledWindowValue3,
         f12 + scaledWindowValue3,
         scaledWindowValue3 / 2.0F,
         6.0
      );
      float vertexTextureValue = scaledWindowValue16 + scaledWindowValue3 + scaledWindowValue4;
      vertexTexture.handleForStringFloat(scaledWindowText, vertexTextureValue, scaledWindowValue13 + scaledWindowValue6, new Color(-1));
      if (corzfontText != null) {
         vertexTexture.handleForStringFloat(corzfontText, vertexTextureValue + scaledWindowValue8 + scaledWindowValue7, scaledWindowValue13 + scaledWindowValue6, new Color(-8421505));
      }
   }

   public static float getAmountFloat() {
      double chestStorageFactor = System.currentTimeMillis() % 2400L / 2400.0;
      return 0.75F + 0.25F * (0.5F + 0.5F * (float)Math.sin(chestStorageFactor * Math.PI * 2.0));
   }

   public static String formatChestStorage(RoleDimension.Role role) {
      if (role == null) {
         return "Storage Chest";
      } else {
         return switch (role) {
            case EMERALDS -> "Emerald Chest";
            case OUTPUTS -> "Output Chest";
            case MATERIALS -> "Material Chest";
            case DROP -> "Drop Spot";
            case ANY -> "Storage Chest";
         };
      }
   }

   public static int getValue(int chestStorageValue, float chestStorageLimit) {
      int max3 = Math.max(0, Math.min(255, Math.round(getAmount(chestStorageLimit) * 255.0F)));
      return max3 << 24 | chestStorageValue & 16777215;
   }

   private static float getAmount(float chestStorageLimit) {
      return chestStorageLimit < 0.0F ? 0.0F : (chestStorageLimit > 1.0F ? 1.0F : chestStorageLimit);
   }

   public static Box getUnion(BlockPos pos2) {
      Box offset = getOffset(pos2);
      BlockPos offsetForBlockPos = getOffsetForBlockPos(pos2);
      return offsetForBlockPos == null ? offset : offset.union(getOffset(offsetForBlockPos));
   }

   private static Box getOffset(BlockPos pos2) {
      MinecraftClient instance = MinecraftClient.getInstance();
      Box box = new Box(pos2);
      if (instance.world == null) {
         return box;
      } else {
         try {
            VoxelShape outlineShape = instance.world.getBlockState(pos2).getOutlineShape(instance.world, pos2);
            return outlineShape.isEmpty() ? box : outlineShape.getBoundingBox().offset(pos2);
         } catch (Exception e) {
            return box;
         }
      }
   }

   public static void handleMinMax(Matrix4f matrix, Box box, int chestStorageValue, float chestStorageFactor, float chestStorageLimit, float blockOverlayValue) {
      float vertexBuilderValue = (float)box.minX - 0.02F;
      float f2 = (float)box.minY - 0.02F;
      float amountForDouble = (float)box.minZ - 0.02F;
      float vertexColorValue = (float)box.maxX + 0.02F;
      float f5 = (float)box.maxY + 0.02F;
      float f6 = (float)box.maxZ + 0.02F;
      if (chestStorageLimit > 0.002F) {
         VertexBuilderUtils.handle(
            matrix, vertexBuilderValue, f2, amountForDouble, vertexColorValue, f5, f6, (chestStorageValue >> 16 & 0xFF) / 255.0F, (chestStorageValue >> 8 & 0xFF) / 255.0F, (chestStorageValue & 0xFF) / 255.0F, getAmount(chestStorageLimit * chestStorageFactor)
         );
      }

      VertexBuilderUtils.handle(matrix, vertexBuilderValue, f2, amountForDouble, vertexColorValue, f5, f6, getValue(0, chestStorageFactor * 0.45F), blockOverlayValue + 2.0F);
      VertexBuilderUtils.handle(matrix, vertexBuilderValue, f2, amountForDouble, vertexColorValue, f5, f6, getValue(chestStorageValue, chestStorageFactor), blockOverlayValue);
   }

   public static BlockPos getOffsetForBlockPos(BlockPos pos2) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.world != null && pos2 != null) {
         BlockState blockState = instance.world.getBlockState(pos2);
         if (blockState.getBlock() instanceof ChestBlock && blockState.contains(ChestBlock.CHEST_TYPE)) {
            return blockState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE ? null : pos2.offset(ChestBlock.getFacing(blockState));
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private ChestStorageUtils() {
   }

   public static void update() {
      OCCUPIED_RECTS.clear();
   }

   public static Vec3d getMaxMin(Box box) {
      return new Vec3d((box.minX + box.maxX) / 2.0, box.maxY + 0.35, (box.minZ + box.maxZ) / 2.0);
   }

   public static int getStorageChest(RoleDimension.Role role) {
      if (role == null) {
         return -4669236;
      } else {
         return switch (role) {
            case EMERALDS -> -12723068;
            case OUTPUTS -> MatrixEspUtils.releaseRightValue | 0xFF000000;
            case MATERIALS -> -871589;
            case DROP -> -13375745;
            case ANY -> -4669236;
         };
      }
   }
}
