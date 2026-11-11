package com.corz.client.render;

import com.corz.client.world.StateDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import com.corz.client.utils.CornerCamUtils;

final class AmbBaked {
   private static final float[] tintTable = new float[]{0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F};
   private final float[] offsetTable;
   private StateDefault stateDefault;
   private float distanceFactor;
   private final float[][] tintFloats;
   private float[] clone;
   private float vectorPositionAmount;
   private float ambBakedAmount;
   private static final float[] ambBakedTable = new float[]{1.0F, 0.78F, 0.62F, 0.5F};
   private final float[][] offsetFloats;
   private int quadCount;
   private float[] copyOf = new float[196608];
   private final float[] ambTable;
   private final float[] vectorPositionTable = new float[16];
   private final Vector3f vectorPositionVector3f = new Vector3f();

   private static boolean canFlameLit(Sprite sprite) {
      if (sprite == null) {
         return true;
      } else {
         String path = sprite.getContents().getId().getPath();
         return path.contains("fire") || path.contains("lantern") || path.contains("_lit") || path.contains("flame");
      }
   }

   private static int getValue(float ambBakedValue) {
      int ambBakedLimit = (int)(ambBakedValue * 255.0F + 0.5F);
      return ambBakedLimit < 0 ? 0 : (ambBakedLimit > 255 ? 255 : ambBakedLimit);
   }

   int getValue() {
      return this.quadCount;
   }

   void handleAmount(float ambBakedFactor, float ambBakedFactor2, float ambBakedFactor3) {
      float distance = (float)Math.sqrt(ambBakedFactor * ambBakedFactor + ambBakedFactor2 * ambBakedFactor2 + ambBakedFactor3 * ambBakedFactor3);
      this.ambBakedAmount = ambBakedFactor / distance;
      this.vectorPositionAmount = ambBakedFactor2 / distance;
      this.distanceFactor = ambBakedFactor3 / distance;
   }

   float[] getAmbAmounts(float ambValue, float ambValue2, float defaultValue, float ambFactor, float ambBound, float ambFactor2) {
      boolean ambFlag = StateDefault.canFlag(defaultValue);
      float ambTotal;
      float ambTotal2;
      float ambTotal3;
      if (ambFlag) {
         float ambValue4 = 0.46F;
         float ambValue5 = 0.55F;
         float ambValue6 = 0.86F;
         float maxFny = Math.max(0.0F, ambBound);
         ambTotal = ambValue4 + 0.18F * maxFny;
         ambTotal2 = ambValue5 + 0.2F * maxFny;
         ambTotal3 = ambValue6 + 0.12F * maxFny;
      } else {
         ambTotal = CornerCamUtils.getAmount("amb.r", 0.17F);
         ambTotal2 = CornerCamUtils.getAmount("amb.g", 0.13F);
         ambTotal3 = CornerCamUtils.getAmount("amb.b", 0.115F);
      }

      float ambTotal4 = 0.0F;
      float ambTotal5 = 0.0F;
      float ambTotal6 = 0.0F;

      for (StateDefault.Light light : this.stateDefault.ambItems) {
         if (!ambFlag || !light.fire()) {
            float ambFactor3 = light.x() - ambValue;
            float ambFactor4 = light.y() - ambValue2;
            float ambFactor5 = light.z() - defaultValue;
            float squaredDistanceTo = ambFactor3 * ambFactor3 + ambFactor4 * ambFactor4 + ambFactor5 * ambFactor5;
            float sqrt = (float)Math.sqrt(squaredDistanceTo);
            float ambValue8 = sqrt < 1.0E-4F ? 1.0F : Math.max(0.0F, (ambFactor3 * ambFactor + ambFactor4 * ambBound + ambFactor5 * ambFactor2) / sqrt);
            float ambFactor6 = 0.25F + 0.75F * ambValue8;
            float ambFactor7 = light.strength() / (1.0F + squaredDistanceTo / (light.radius() * light.radius()));
            if (ambFlag) {
               ambFactor7 *= 0.35F;
            }

            float ambFactor8 = ambFactor7 * ambFactor6;
            if (light.fire()) {
               ambTotal4 += light.r() * ambFactor8;
               ambTotal5 += light.g() * ambFactor8;
               ambTotal6 += light.b() * ambFactor8;
            } else {
               ambTotal += light.r() * ambFactor8;
               ambTotal2 += light.g() * ambFactor8;
               ambTotal3 += light.b() * ambFactor8;
            }
         }
      }

      this.ambTable[0] = ambTotal;
      this.ambTable[1] = ambTotal2;
      this.ambTable[2] = ambTotal3;
      this.ambTable[3] = ambTotal4;
      this.ambTable[4] = ambTotal5;
      this.ambTable[5] = ambTotal6;
      return this.ambTable;
   }

   private void handleAmountsTint(
      BakedQuad bakedQuad, BlockState blockState, BlockColors blockColors, float tintValue, float valueInt, float valueForLong, int offsetValue, int y, int z, boolean tintFlag, boolean offsetQuadsFlag
   ) {
      float tintTotal = 0.0F;
      float tintTotal2 = 0.0F;
      float tintTotal3 = 0.0F;

      for (int i = 0; i < 4; i++) {
         Vector3fc position = bakedQuad.getPosition(i);
         this.offsetFloats[i][0] = tintValue + position.x();
         this.offsetFloats[i][1] = valueInt + position.y();
         this.offsetFloats[i][2] = valueForLong + position.z();
         tintTotal += this.offsetFloats[i][0];
         tintTotal2 += this.offsetFloats[i][1];
         tintTotal3 += this.offsetFloats[i][2];
         long texcoords = bakedQuad.getTexcoords(i);
         this.tintFloats[i][0] = Vector2f.getX(texcoords);
         this.tintFloats[i][1] = Vector2f.getY(texcoords);
      }

      float tintValue7 = tintTotal * 0.25F;
      float tintValue8 = tintTotal2 * 0.25F;
      float tintValue9 = tintTotal3 * 0.25F;
      float tintFactor = this.offsetFloats[1][0] - this.offsetFloats[0][0];
      float tintFactor2 = this.offsetFloats[1][1] - this.offsetFloats[0][1];
      float tintFactor3 = this.offsetFloats[1][2] - this.offsetFloats[0][2];
      float tintFactor4 = this.offsetFloats[2][0] - this.offsetFloats[0][0];
      float tintFactor5 = this.offsetFloats[2][1] - this.offsetFloats[0][1];
      float tintFactor6 = this.offsetFloats[2][2] - this.offsetFloats[0][2];
      float tintFactor7 = tintFactor2 * tintFactor6 - tintFactor3 * tintFactor5;
      float tintFactor8 = tintFactor3 * tintFactor4 - tintFactor * tintFactor6;
      float tintFactor9 = tintFactor * tintFactor5 - tintFactor2 * tintFactor4;
      float distance = (float)Math.sqrt(tintFactor7 * tintFactor7 + tintFactor8 * tintFactor8 + tintFactor9 * tintFactor9);
      if (!(distance < 1.0E-6F)) {
         float ambFactor = tintFactor7 / distance;
         float ambBound = tintFactor8 / distance;
         float tintFactor12 = tintFactor9 / distance;
         float tintBound = -Float.MAX_VALUE;

         for (int j = 0; j + 2 < this.clone.length; j += 3) {
            tintBound = Math.max(tintBound, ambFactor * (this.clone[j] - tintValue7) + ambBound * (this.clone[j + 1] - tintValue8) + tintFactor12 * (this.clone[j + 2] - tintValue9));
         }

         if (!(tintBound < -0.35F)) {
            Direction face = bakedQuad.face();
            int index = face == null ? 1 : face.ordinal();
            int tintValue10 = bakedQuad.hasTint() ? blockColors.getColor(blockState, null, null, bakedQuad.tintIndex()) : -1;
            float tintFactor13 = 1.0F;
            float tintFactor14 = 1.0F;
            float tintFactor15 = 1.0F;
            if (bakedQuad.hasTint() && tintValue10 != -1) {
               tintFactor13 = (tintValue10 >> 16 & 0xFF) / 255.0F;
               tintFactor14 = (tintValue10 >> 8 & 0xFF) / 255.0F;
               tintFactor15 = (tintValue10 & 0xFF) / 255.0F;
            }

            float tintFactor16 = bakedQuad.shade() ? tintTable[index] : 1.0F;
            boolean tintFlag3 = tintFlag && canFlameLit(bakedQuad.sprite());
            if (offsetQuadsFlag && bakedQuad.shade() && face != null && !tintFlag3) {
               this.handleAmountsOffset(face, offsetValue, y, z);
            } else {
               for (int k = 0; k < 4; k++) {
                  this.offsetTable[k] = 1.0F;
               }
            }

            for (int m = 0; m < 4; m++) {
               float tintFactor17 = tintFactor13 * tintFactor16 * this.offsetTable[m];
               float tintFactor18 = tintFactor14 * tintFactor16 * this.offsetTable[m];
               float tintFactor19 = tintFactor15 * tintFactor16 * this.offsetTable[m];
               float ambBakedValue = this.offsetFloats[m][0];
               float tintValue12 = this.offsetFloats[m][1];
               float defaultValue = this.offsetFloats[m][2];
               if (tintFlag3) {
                  this.handle(ambBakedValue, tintValue12, defaultValue, this.tintFloats[m][0], this.tintFloats[m][1], tintFactor13, tintFactor14, tintFactor15, 0.0F, 0.0F, 0.0F, 0.0F);
               } else {
                  float[] ambAmounts = this.getAmbAmounts(ambBakedValue + ambFactor * 0.05F, tintValue12 + ambBound * 0.05F, defaultValue + tintFactor12 * 0.05F, ambFactor, ambBound, tintFactor12);
                  this.handle(
                     ambBakedValue,
                     tintValue12,
                     defaultValue,
                     this.tintFloats[m][0],
                     this.tintFloats[m][1],
                     tintFactor17 * ambAmounts[0],
                     tintFactor18 * ambAmounts[1],
                     tintFactor19 * ambAmounts[2],
                     tintFactor17 * ambAmounts[3],
                     tintFactor18 * ambAmounts[4],
                     tintFactor19 * ambAmounts[5],
                     getAmount(ambBakedValue, defaultValue)
                  );
               }
            }
         }
      }
   }

   AmbBaked() {
      this.offsetFloats = new float[4][3];
      this.tintFloats = new float[4][2];
      this.offsetTable = new float[4];
      this.clone = new float[3];
      this.ambTable = new float[6];
   }

   private void handleOffsetQuads(
      BlockModels blockModels, BlockColors blockColors, List<BlockModelPart> list, BlockState blockState, float offsetQuadsValue, float valueInt, float valueForLong, int x, int y, int z, boolean offsetQuadsFlag
   ) {
      if (blockState.getRenderType() == BlockRenderType.MODEL) {
         BlockStateModel model = blockModels.getModel(blockState);
         list.clear();
         model.addParts(Random.create(blockState.getRenderingSeed(new BlockPos(x, y, z))), list);
         boolean tintFlag = blockState.getLuminance() > 0;

         for (BlockModelPart blockModelPart : list) {
            for (Direction direction : Direction.values()) {
               if (!offsetQuadsFlag || !this.stateDefault.canOpaqueCube(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ())) {
                  for (BakedQuad bakedQuad : blockModelPart.getQuads(direction)) {
                     this.handleAmountsTint(bakedQuad, blockState, blockColors, offsetQuadsValue, valueInt, valueForLong, x, y, z, tintFlag, offsetQuadsFlag);
                  }
               }
            }

            for (BakedQuad getQuadsItem : blockModelPart.getQuads(null)) {
               this.handleAmountsTint(getQuadsItem, blockState, blockColors, offsetQuadsValue, valueInt, valueForLong, x, y, z, tintFlag, offsetQuadsFlag);
            }
         }
      }
   }

   private void handleAmountsOffset(Direction direction, int offsetValue, int y, int z) {
      int offsetValue4 = offsetValue + direction.getOffsetX();
      int offsetValue5 = y + direction.getOffsetY();
      int offsetValue6 = z + direction.getOffsetZ();
      Axis axis = direction.getAxis();

      for (int i = 0; i < 4; i++) {
         float offsetLimit = this.offsetFloats[i][0] - offsetValue;
         float offsetLimit2 = this.offsetFloats[i][1] - y;
         float offsetLimit3 = this.offsetFloats[i][2] - z;
         int offsetValue7 = axis == Axis.X ? 0 : (offsetLimit > 0.75F ? 1 : (offsetLimit < 0.25F ? -1 : 0));
         int offsetValue8 = axis == Axis.Y ? 0 : (offsetLimit2 > 0.75F ? 1 : (offsetLimit2 < 0.25F ? -1 : 0));
         int offsetValue9 = axis == Axis.Z ? 0 : (offsetLimit3 > 0.75F ? 1 : (offsetLimit3 < 0.25F ? -1 : 0));
         int offsetValue10;
         int offsetValue11;
         byte offsetValue12;
         byte offsetValue13;
         int offsetValue14;
         int offsetValue15;
         if (axis == Axis.X) {
            offsetValue10 = 0;
            offsetValue11 = offsetValue8;
            offsetValue12 = 0;
            offsetValue13 = 0;
            offsetValue14 = 0;
            offsetValue15 = offsetValue9;
         } else if (axis == Axis.Y) {
            offsetValue10 = offsetValue7;
            offsetValue11 = 0;
            offsetValue12 = 0;
            offsetValue13 = 0;
            offsetValue14 = 0;
            offsetValue15 = offsetValue9;
         } else {
            offsetValue10 = offsetValue7;
            offsetValue11 = 0;
            offsetValue12 = 0;
            offsetValue13 = 0;
            offsetValue14 = offsetValue8;
            offsetValue15 = 0;
         }

         boolean offsetFlag = (offsetValue10 | offsetValue11 | offsetValue12) != 0 && this.stateDefault.canOpaqueCube(offsetValue4 + offsetValue10, offsetValue5 + offsetValue11, offsetValue6 + offsetValue12);
         boolean offsetFlag2 = (offsetValue13 | offsetValue14 | offsetValue15) != 0 && this.stateDefault.canOpaqueCube(offsetValue4 + offsetValue13, offsetValue5 + offsetValue14, offsetValue6 + offsetValue15);
         boolean offsetFlag3 = (offsetValue10 | offsetValue11 | offsetValue12) != 0
            && (offsetValue13 | offsetValue14 | offsetValue15) != 0
            && this.stateDefault.canOpaqueCube(offsetValue4 + offsetValue10 + offsetValue13, offsetValue5 + offsetValue11 + offsetValue14, offsetValue6 + offsetValue12 + offsetValue15);
         int offsetBound = offsetFlag && offsetFlag2 ? 3 : (offsetFlag ? 1 : 0) + (offsetFlag2 ? 1 : 0) + (offsetFlag3 ? 1 : 0);
         this.offsetTable[i] = ambBakedTable[Math.min(3, offsetBound)];
      }
   }

   private static float getAmount(float ambBakedValue, float defaultValue) {
      return Math.floorMod((int)Math.floor(ambBakedValue * 1.7F + defaultValue * 1.1F), 16);
   }

   void handleColorsModels(StateDefault stateDefault, float... colorsModelsValues) {
      this.stateDefault = stateDefault;
      this.clone = (float[])colorsModelsValues.clone();
      this.quadCount = 0;
      MinecraftClient instance = MinecraftClient.getInstance();
      BlockModels models = instance.getBlockRenderManager().getModels();
      BlockColors blockColors = instance.getBlockColors();
      ArrayList arrayList = new ArrayList();

      for (Entry entry : stateDefault.colorsModelsBlockStateMap.entrySet()) {
         long colorsModelsValue = (Long)entry.getKey();
         int offsetQuadsValue = StateDefault.getValue(colorsModelsValue);
         int valueInt = StateDefault.getValueInt(colorsModelsValue);
         int valueForLong = StateDefault.getValueForLong(colorsModelsValue);
         this.handleOffsetQuads(models, blockColors, arrayList, (BlockState)entry.getValue(), offsetQuadsValue, valueInt, valueForLong, offsetQuadsValue, valueInt, valueForLong, true);
      }

      for (StateDefault.Decor decor : stateDefault.defaultDecorList) {
         int x = (int)Math.floor(decor.x());
         int y = (int)Math.floor(decor.y());
         int z = (int)Math.floor(decor.z());
         this.handleOffsetQuads(models, blockColors, arrayList, decor.state(), decor.x(), decor.y(), decor.z(), x, y, z, false);
      }
   }

   void handleAmounts(double ambBakedValue) {
      for (int i = 0; i < 16; i++) {
         double ambBakedValue2 = i * 0.73;
         float ambBakedBound = 0.84F
            + 0.09F * (float)Math.sin(ambBakedValue * 9.7 + ambBakedValue2)
            + 0.05F * (float)Math.sin(ambBakedValue * 23.3 + ambBakedValue2 * 2.1)
            + 0.04F * (float)Math.sin(ambBakedValue * 4.1 + ambBakedValue2 * 0.6);
         this.vectorPositionTable[i] = Math.max(0.62F, Math.min(1.05F, ambBakedBound));
      }
   }

   private void handle(float ambBakedValue, float ambBakedValue2, float defaultValue, float ambBakedValue4, float ambBakedValue5, float ambBakedValue6, float ambBakedValue7, float ambBakedValue8, float ambBakedValue9, float ambBakedValue10, float ambBakedValue11, float ambBakedValue12) {
      int ambBakedLimit = (this.quadCount + 1) * 12;
      if (ambBakedLimit > this.copyOf.length) {
         this.copyOf = Arrays.copyOf(this.copyOf, this.copyOf.length * 2);
      }

      int index = this.quadCount * 12;
      this.copyOf[index] = ambBakedValue;
      this.copyOf[index + 1] = ambBakedValue2;
      this.copyOf[index + 2] = defaultValue;
      this.copyOf[index + 3] = ambBakedValue4;
      this.copyOf[index + 4] = ambBakedValue5;
      this.copyOf[index + 5] = ambBakedValue6;
      this.copyOf[index + 6] = ambBakedValue7;
      this.copyOf[index + 7] = ambBakedValue8;
      this.copyOf[index + 8] = ambBakedValue9;
      this.copyOf[index + 9] = ambBakedValue10;
      this.copyOf[index + 10] = ambBakedValue11;
      this.copyOf[index + 11] = ambBakedValue12;
      this.quadCount++;
   }

   float getAmounts(float ambBakedValue, float defaultValue) {
      return this.vectorPositionTable[(int)getAmount(ambBakedValue, defaultValue)];
   }

   void handleVectorPosition(net.minecraft.client.util.math.MatrixStack.Entry entry, VertexConsumer vertices) {
      Matrix4f positionMatrix = entry.getPositionMatrix();
      float[] copyOf = this.copyOf;
      int vectorPositionCounter = 0;

      for (int i = 0; vectorPositionCounter < this.quadCount; i += 12) {
         positionMatrix.transformPosition(copyOf[i], copyOf[i + 1], copyOf[i + 2], this.vectorPositionVector3f);
         float vectorPositionFactor = this.vectorPositionTable[(int)copyOf[i + 11]];
         int vectorPositionValue = getValue(copyOf[i + 5] + copyOf[i + 8] * vectorPositionFactor);
         int vectorPositionValue2 = getValue(copyOf[i + 6] + copyOf[i + 9] * vectorPositionFactor);
         int vectorPositionValue3 = getValue(copyOf[i + 7] + copyOf[i + 10] * vectorPositionFactor);
         vertices.vertex(
            this.vectorPositionVector3f.x,
            this.vectorPositionVector3f.y,
            this.vectorPositionVector3f.z,
            0xFF000000 | vectorPositionValue << 16 | vectorPositionValue2 << 8 | vectorPositionValue3,
            copyOf[i + 3],
            copyOf[i + 4],
            OverlayTexture.DEFAULT_UV,
            15728880,
            this.ambBakedAmount,
            this.vectorPositionAmount,
            this.distanceFactor
         );
         vectorPositionCounter++;
      }
   }
}
