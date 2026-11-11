package com.corz.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

final class ModelPartUtils {
   static final Identifier SANTA_SKIN_TEXTURE = getIdentifier("santa_skin");
   static final Identifier GINGERBREAD_TEXTURE = getIdentifier("gingerbread");
   static final Identifier STOCKING_TEXTURE = getIdentifier("stocking");
   static final Identifier STAR_TEXTURE = getIdentifier("star");
   static final Identifier ROPE_TEXTURE = getIdentifier("rope");
   static final Identifier GLOW_TEXTURE = getIdentifier("glow");
   static final Identifier BULB_TEXTURE = getIdentifier("bulb");
   static final Identifier IDENTIFIER = getIdentifier("sleigh");
   static final Identifier REINDEER_TEXTURE = getIdentifier("reindeer");
   static final Identifier MUG_TEXTURE = getIdentifier("mug");
   static final Identifier[] presentBlueIdentifiers = new Identifier[]{
      getIdentifier("present_red"), getIdentifier("present_green"), getIdentifier("present_gold"), getIdentifier("present_blue")
   };
   private static boolean identifierIdentifiersActive;
   private static final int[][] identifierIdentifiersInts = new int[][]{{-3790022, -727352}, {-13726889, -1558454}, {-1459895, -3790022}, {-12881960, -724502}};

   private ModelPartUtils() {
   }

   private static NativeImage getArgbColor() {
      NativeImage nativeImage = new NativeImage(16, 16, true);
      handleArgbColor(nativeImage, 0, 0, 16, 8, -3790022, -6283732);

      for (int i = 0; i < 12; i++) {
         nativeImage.setColorArgb(i, 5, -725536);
      }

      handleArgbColor(nativeImage, 0, 0, 6, 3, -9749729, -11654892);
      handleArgbColor(nativeImage, 0, 8, 16, 8, -8764638, -10604520);

      for (int j = 2; j < 6; j++) {
         nativeImage.setColorArgb(j, 9, -462104);
      }

      return nativeImage;
   }

   static ModelPart getModel() {
      ModelData modelData = new ModelData();
      ModelPartData root = modelData.getRoot();
      root.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -12.0F, -2.5F, 12.0F, 5.0F, 5.0F), ModelTransform.NONE);
      ModelPartData addChild = root.addChild(
         "neck", ModelPartBuilder.create().uv(34, 0).cuboid(-1.5F, -6.0F, -1.5F, 3.0F, 6.0F, 3.0F), ModelTransform.of(5.0F, -11.0F, 0.0F, 0.0F, 0.0F, -0.45F)
      );
      ModelPartData addChild2 = addChild.addChild(
         "head", ModelPartBuilder.create().uv(0, 10).cuboid(-1.5F, -2.5F, -2.0F, 5.0F, 3.0F, 4.0F), ModelTransform.origin(0.5F, -6.0F, 0.0F)
      );
      addChild2.addChild("nose", ModelPartBuilder.create().uv(18, 10).cuboid(3.4F, -2.0F, -1.0F, 1.0F, 1.5F, 2.0F), ModelTransform.NONE);
      addChild2.addChild(
         "antler_l",
         ModelPartBuilder.create().uv(24, 10).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F).uv(24, 10).cuboid(-2.5F, -4.0F, -0.5F, 2.0F, 1.0F, 1.0F),
         ModelTransform.of(0.0F, -2.5F, -1.2F, -0.3F, 0.0F, 0.0F)
      );
      addChild2.addChild(
         "antler_r",
         ModelPartBuilder.create().uv(24, 10).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F).uv(24, 10).cuboid(-2.5F, -4.0F, -0.5F, 2.0F, 1.0F, 1.0F),
         ModelTransform.of(0.0F, -2.5F, 1.2F, 0.3F, 0.0F, 0.0F)
      );
      root.addChild("leg_fl", ModelPartBuilder.create().uv(30, 10).cuboid(-0.75F, 0.0F, -0.75F, 1.5F, 7.0F, 1.5F), ModelTransform.origin(4.5F, -7.0F, -1.4F));
      root.addChild("leg_fr", ModelPartBuilder.create().uv(30, 10).cuboid(-0.75F, 0.0F, -0.75F, 1.5F, 7.0F, 1.5F), ModelTransform.origin(4.5F, -7.0F, 1.4F));
      root.addChild("leg_bl", ModelPartBuilder.create().uv(30, 10).cuboid(-0.75F, 0.0F, -0.75F, 1.5F, 7.0F, 1.5F), ModelTransform.origin(-4.5F, -7.0F, -1.4F));
      root.addChild("leg_br", ModelPartBuilder.create().uv(30, 10).cuboid(-0.75F, 0.0F, -0.75F, 1.5F, 7.0F, 1.5F), ModelTransform.origin(-4.5F, -7.0F, 1.4F));
      root.addChild("tail", ModelPartBuilder.create().uv(38, 10).cuboid(-1.5F, -1.0F, -0.75F, 1.5F, 1.5F, 1.5F), ModelTransform.origin(-6.0F, -11.5F, 0.0F));
      return TexturedModelData.of(modelData, 64, 32).createModel();
   }

   static ModelPart getModelModelPart() {
      ModelData modelData = new ModelData();
      ModelPartData root = modelData.getRoot();
      root.addChild("tub", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, -9.0F, -6.0F, 20.0F, 5.0F, 12.0F), ModelTransform.NONE);
      root.addChild("back", ModelPartBuilder.create().uv(0, 17).cuboid(-11.0F, -15.0F, -6.0F, 4.0F, 7.0F, 12.0F), ModelTransform.NONE);
      root.addChild("curl", ModelPartBuilder.create().uv(32, 17).cuboid(9.0F, -12.0F, -6.0F, 3.0F, 4.0F, 12.0F), ModelTransform.NONE);
      root.addChild("seat", ModelPartBuilder.create().uv(64, 0).cuboid(-7.0F, -11.0F, -5.0F, 6.0F, 2.0F, 10.0F), ModelTransform.NONE);
      root.addChild("sack", ModelPartBuilder.create().uv(64, 12).cuboid(-6.0F, -16.0F, -3.5F, 6.0F, 7.0F, 7.0F), ModelTransform.NONE);
      root.addChild("runner_l", ModelPartBuilder.create().uv(0, 40).cuboid(-11.0F, -1.0F, -6.0F, 24.0F, 1.0F, 1.0F), ModelTransform.NONE);
      root.addChild("runner_r", ModelPartBuilder.create().uv(0, 40).cuboid(-11.0F, -1.0F, 5.0F, 24.0F, 1.0F, 1.0F), ModelTransform.NONE);
      root.addChild("tip_l", ModelPartBuilder.create().uv(0, 44).cuboid(12.0F, -4.0F, -6.0F, 1.0F, 4.0F, 1.0F), ModelTransform.NONE);
      root.addChild("tip_r", ModelPartBuilder.create().uv(0, 44).cuboid(12.0F, -4.0F, 5.0F, 1.0F, 4.0F, 1.0F), ModelTransform.NONE);
      root.addChild(
         "strut",
         ModelPartBuilder.create().uv(8, 44).cuboid(-7.0F, -4.0F, -6.0F, 1.0F, 3.0F, 12.0F).uv(8, 44).cuboid(5.0F, -4.0F, -6.0F, 1.0F, 3.0F, 12.0F),
         ModelTransform.NONE
      );
      return TexturedModelData.of(modelData, 128, 64).createModel();
   }

   private static void handleArgbColor(NativeImage nativeImage, int argbColorValue, int argbColorValue2, int nativeImageValue, int argbColorValue4, int modelPartValue, int argbColorValue6) {
      for (int i = argbColorValue2; i < argbColorValue2 + argbColorValue4; i++) {
         for (int j = argbColorValue; j < argbColorValue + nativeImageValue; j++) {
            nativeImage.setColorArgb(j, i, getValue(modelPartValue, argbColorValue6, getAmount(j, i) * 0.35F));
         }
      }
   }

   static ModelPart getModel3() {
      ModelData modelData = new ModelData();
      ModelPartData root = modelData.getRoot();
      root.addChild("v", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -4.0F, -0.5F, 2.0F, 8.0F, 1.0F), ModelTransform.NONE);
      root.addChild("h", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -1.0F, -0.5F, 8.0F, 2.0F, 1.0F), ModelTransform.NONE);
      root.addChild("d1", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -3.0F, -0.4F, 2.0F, 6.0F, 1.0F), ModelTransform.rotation(0.0F, 0.0F, 0.785F));
      root.addChild("d2", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -3.0F, -0.4F, 2.0F, 6.0F, 1.0F), ModelTransform.rotation(0.0F, 0.0F, -0.785F));
      return TexturedModelData.of(modelData, 16, 16).createModel();
   }

   private static NativeImage getNativeImage(int nativeImageValue, int nativeImageValue2, int modelPartValue, int nativeImageValue4) {
      NativeImage nativeImage = new NativeImage(nativeImageValue, nativeImageValue2, true);
      handleArgbColor(nativeImage, 0, 0, nativeImageValue, nativeImageValue2, modelPartValue, nativeImageValue4);
      return nativeImage;
   }

   static void handleIdentifierIdentifiers(MinecraftClient client) {
      if (!identifierIdentifiersActive) {
         identifierIdentifiersActive = true;
         handleTextureRegister(client, SANTA_SKIN_TEXTURE, getArgbColor5());
         handleTextureRegister(client, GINGERBREAD_TEXTURE, getArgbColor4());
         handleTextureRegister(client, STOCKING_TEXTURE, getArgbColor3());
         handleTextureRegister(client, STAR_TEXTURE, getNativeImage(16, 16, -11443, -1529554));
         handleTextureRegister(client, ROPE_TEXTURE, getArgbColorNativeImage());
         handleTextureRegister(client, GLOW_TEXTURE, getArgbColor7());
         handleTextureRegister(client, BULB_TEXTURE, getNativeImage(8, 8, -1, -1));
         handleTextureRegister(client, IDENTIFIER, getArgbColor6());
         handleTextureRegister(client, REINDEER_TEXTURE, getNativeImage());
         handleTextureRegister(client, MUG_TEXTURE, getArgbColor());

         for (int i = 0; i < presentBlueIdentifiers.length; i++) {
            handleTextureRegister(client, presentBlueIdentifiers[i], getNativeImage(identifierIdentifiersInts[i][0], identifierIdentifiersInts[i][1]));
         }
      }
   }

   static ModelPart getModel4() {
      ModelData modelData = new ModelData();
      modelData.getRoot().addChild("b", ModelPartBuilder.create().uv(0, 0).cuboid(-0.75F, -0.75F, -0.75F, 1.5F, 1.5F, 1.5F), ModelTransform.NONE);
      return TexturedModelData.of(modelData, 8, 8).createModel();
   }

   private static NativeImage getArgbColorNativeImage() {
      NativeImage nativeImage = new NativeImage(8, 32, true);

      for (int i = 0; i < 32; i++) {
         for (int j = 0; j < 8; j++) {
            nativeImage.setColorArgb(j, i, (j + i) % 4 < 2 ? -6523832 : -9482190);
         }
      }

      return nativeImage;
   }

   static ModelPart getModel5() {
      ModelData modelData = new ModelData();
      ModelPartData root = modelData.getRoot();
      root.addChild("cup", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -3.5F, -1.5F, 3.0F, 3.5F, 3.0F), ModelTransform.NONE);
      root.addChild("cocoa", ModelPartBuilder.create().uv(0, 8).cuboid(-1.2F, -3.3F, -1.2F, 2.4F, 0.4F, 2.4F), ModelTransform.NONE);
      root.addChild("handle", ModelPartBuilder.create().uv(12, 0).cuboid(1.5F, -2.8F, -0.4F, 1.0F, 2.0F, 0.8F), ModelTransform.NONE);
      return TexturedModelData.of(modelData, 16, 16).createModel();
   }

   private static NativeImage getArgbColor3() {
      NativeImage nativeImage = new NativeImage(32, 16, true);
      handleArgbColor(nativeImage, 0, 0, 14, 5, -592914, -2501428);
      handleArgbColor(nativeImage, 0, 5, 32, 11, -3924434, -7465952);

      for (int i = 0; i < 32; i += 3) {
         nativeImage.setColorArgb(i, 9, -13726889);
      }

      return nativeImage;
   }

   static ModelPart getModel6() {
      ModelData modelData = new ModelData();
      modelData.getRoot().addChild("rope", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 16.0F, 1.0F), ModelTransform.NONE);
      return TexturedModelData.of(modelData, 8, 32).createModel();
   }

   private static void handleTextureRegister(MinecraftClient client, Identifier id2, NativeImage nativeImage) {
      if (id2 == GLOW_TEXTURE) {
         client.getTextureManager()
            .registerTexture(
               id2,
               new NativeImageBackedTexture(() -> "corz " + id2.getPath(), nativeImage) {
                  {
                     this.sampler = RenderSystem.getSamplerCache()
                        .get(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, false);
                  }
               }
            );
      } else {
         client.getTextureManager().registerTexture(id2, new NativeImageBackedTexture(() -> "corz " + id2.getPath(), nativeImage));
      }
   }

   private static NativeImage getArgbColor4() {
      NativeImage nativeImage = new NativeImage(32, 32, true);
      int argbColorValue = -4689094;
      int argbColorValue2 = -7449562;
      short argbColorValue3 = -2322;
      handleArgbColor(nativeImage, 0, 0, 32, 32, argbColorValue, argbColorValue2);
      nativeImage.setColorArgb(4, 5, -14018030);
      nativeImage.setColorArgb(7, 5, -14018030);

      for (int i = 4; i <= 7; i++) {
         nativeImage.setColorArgb(i, 7, argbColorValue3);
      }

      nativeImage.setColorArgb(3, 6, argbColorValue3);
      nativeImage.setColorArgb(8, 6, argbColorValue3);
      nativeImage.setColorArgb(5, 13, -1558454);
      nativeImage.setColorArgb(5, 15, -12723068);
      nativeImage.setColorArgb(5, 17, argbColorValue3);

      for (int j = 18; j < 26; j += 2) {
         nativeImage.setColorArgb(j, 5, argbColorValue3);
         nativeImage.setColorArgb(j + 1, 6, argbColorValue3);
         nativeImage.setColorArgb(j, 14, argbColorValue3);
         nativeImage.setColorArgb(j + 1, 15, argbColorValue3);
      }

      return nativeImage;
   }

   static ModelPart getModel7() {
      ModelData modelData = new ModelData();
      ModelPartData root = modelData.getRoot();
      root.addChild("box", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F), ModelTransform.NONE);
      ModelPartData addChild = root.addChild(
         "lid", ModelPartBuilder.create().uv(0, 14).cuboid(-4.5F, -2.0F, -4.5F, 9.0F, 2.0F, 9.0F), ModelTransform.origin(0.0F, -6.0F, 0.0F)
      );
      addChild.addChild(
         "bow_l", ModelPartBuilder.create().uv(36, 14).cuboid(-3.0F, -2.0F, -0.5F, 3.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 0.6F, 0.35F)
      );
      addChild.addChild(
         "bow_r", ModelPartBuilder.create().uv(36, 14).cuboid(0.0F, -2.0F, -0.5F, 3.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 0.6F, -0.35F)
      );
      return TexturedModelData.of(modelData, 64, 32).createModel();
   }

   static ModelPart getModel8() {
      ModelData modelData = new ModelData();
      ModelPartData root = modelData.getRoot();
      root.addChild("cuff", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, 0.0F, -1.5F, 4.0F, 2.0F, 3.0F), ModelTransform.NONE);
      root.addChild("leg", ModelPartBuilder.create().uv(0, 5).cuboid(-1.5F, 2.0F, -1.0F, 3.0F, 6.0F, 2.0F), ModelTransform.NONE);
      root.addChild(
         "foot", ModelPartBuilder.create().uv(10, 5).cuboid(-1.5F, 6.0F, -1.0F, 3.0F, 2.0F, 2.0F), ModelTransform.of(1.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
      );
      return TexturedModelData.of(modelData, 32, 16).createModel();
   }

   static int getValue(int modelPartValue, int modelPartValue2, float modelPartFactor) {
      int modelPartValue3 = modelPartValue >> 16 & 0xFF;
      int modelPartValue4 = modelPartValue >> 8 & 0xFF;
      int modelPartValue5 = modelPartValue & 0xFF;
      int modelPartValue6 = modelPartValue >>> 24;
      int modelPartValue7 = (int)(modelPartValue3 + ((modelPartValue2 >> 16 & 0xFF) - modelPartValue3) * modelPartFactor);
      int modelPartValue8 = (int)(modelPartValue4 + ((modelPartValue2 >> 8 & 0xFF) - modelPartValue4) * modelPartFactor);
      int modelPartValue9 = (int)(modelPartValue5 + ((modelPartValue2 & 0xFF) - modelPartValue5) * modelPartFactor);
      int modelPartValue10 = (int)(modelPartValue6 + ((modelPartValue2 >>> 24) - modelPartValue6) * modelPartFactor);
      return modelPartValue10 << 24 | modelPartValue7 << 16 | modelPartValue8 << 8 | modelPartValue9;
   }

   static Identifier getIdentifier(String identifierText) {
      return Identifier.of("corzclient", "dynamic/xmas_" + identifierText);
   }

   private static NativeImage getNativeImage() {
      NativeImage nativeImage = new NativeImage(64, 32, true);
      handleArgbColor(nativeImage, 0, 0, 64, 32, -8760782, -10600924);
      handleArgbColor(nativeImage, 0, 5, 34, 5, -6524856, -8760782);
      handleArgbColor(nativeImage, 18, 10, 6, 4, -14018030, -15069174);
      handleArgbColor(nativeImage, 24, 10, 6, 8, -2506086, -4677512);
      handleArgbColor(nativeImage, 30, 10, 8, 10, -11915232, -13229553);
      handleArgbColor(nativeImage, 38, 10, 6, 4, -725536, -2568256);
      return nativeImage;
   }

   private static void handleArgbColor(NativeImage nativeImage, int argbColorValue, int argbColorValue2, int argbColorValue3, int argbColorValue4, int argbColorValue5) {
      for (int i = argbColorValue2; i < argbColorValue2 + argbColorValue4; i++) {
         for (int j = argbColorValue; j < argbColorValue + argbColorValue3; j++) {
            nativeImage.setColorArgb(j, i, argbColorValue5);
         }
      }
   }

   private static NativeImage getNativeImage(int nativeImageValue, int nativeImageValue2) {
      NativeImage nativeImage = new NativeImage(64, 32, true);
      handleArgbColor(nativeImage, 0, 0, 64, 32, nativeImageValue, getValue(nativeImageValue, -16777216, 0.18F));

      for (int i = 0; i < 14; i++) {
         handleArgbColor(nativeImage, 11, i, 2, 1, nativeImageValue2);
         handleArgbColor(nativeImage, 27, i, 2, 1, nativeImageValue2);
         handleArgbColor(nativeImage, 3, i, 2, 1, nativeImageValue2);
         handleArgbColor(nativeImage, 19, i, 2, 1, nativeImageValue2);
      }

      handleArgbColor(nativeImage, 8, 3, 8, 2, nativeImageValue2);
      handleArgbColor(nativeImage, 11, 0, 2, 8, nativeImageValue2);

      for (int j = 14; j < 25; j++) {
         handleArgbColor(nativeImage, 13, j, 2, 1, nativeImageValue2);
         handleArgbColor(nativeImage, 31, j, 2, 1, nativeImageValue2);
         handleArgbColor(nativeImage, 4, j, 2, 1, nativeImageValue2);
         handleArgbColor(nativeImage, 22, j, 2, 1, nativeImageValue2);
      }

      handleArgbColor(nativeImage, 9, 18, 9, 2, nativeImageValue2);
      handleArgbColor(nativeImage, 36, 14, 8, 3, nativeImageValue2);
      return nativeImage;
   }

   private static float getAmount(int modelPartValue, int i) {
      int modelPartValue3 = modelPartValue * 73856093 ^ i * 19349663;
      int modelPartValue4 = (modelPartValue3 ^ modelPartValue3 >>> 13) * 1540483477;
      return (modelPartValue4 >>> 8 & 0xFF) / 255.0F;
   }

   private static NativeImage getArgbColor5() {
      NativeImage nativeImage = new NativeImage(64, 64, true);
      int argbColorValue = -3924434;
      int argbColorValue2 = -6679518;
      int modelPartValue = -592914;
      int argbColorValue4 = -2501428;
      int argbColorValue5 = -866651;
      int argbColorValue6 = -1537398;
      int argbColorValue7 = -13948092;
      int argbColorValue8 = -15066598;
      int argbColorValue9 = -1525686;
      handleArgbColor(nativeImage, 0, 0, 32, 16, modelPartValue, argbColorValue4);
      handleArgbColor(nativeImage, 8, 0, 8, 8, argbColorValue);
      handleArgbColor(nativeImage, 8, 8, 8, 1, argbColorValue);
      handleArgbColor(nativeImage, 8, 9, 8, 1, modelPartValue);
      handleArgbColor(nativeImage, 8, 10, 8, 3, argbColorValue5);
      nativeImage.setColorArgb(9, 10, modelPartValue);
      nativeImage.setColorArgb(10, 10, modelPartValue);
      nativeImage.setColorArgb(13, 10, modelPartValue);
      nativeImage.setColorArgb(14, 10, modelPartValue);
      nativeImage.setColorArgb(10, 11, argbColorValue7);
      nativeImage.setColorArgb(13, 11, argbColorValue7);
      nativeImage.setColorArgb(9, 12, argbColorValue6);
      nativeImage.setColorArgb(14, 12, argbColorValue6);
      nativeImage.setColorArgb(11, 12, -1464182);
      nativeImage.setColorArgb(12, 12, -1464182);
      handleArgbColor(nativeImage, 8, 13, 8, 3, modelPartValue);

      for (int argbColorValue10 : new int[]{0, 16, 24}) {
         handleArgbColor(nativeImage, argbColorValue10, 8, 8, 1, argbColorValue);
         handleArgbColor(nativeImage, argbColorValue10, 9, 8, 1, modelPartValue);
      }

      handleArgbColor(nativeImage, 40, 13, 8, 3, modelPartValue);
      handleArgbColor(nativeImage, 48, 0, 8, 8, 0);
      handleArgbColor(nativeImage, 48, 5, 8, 3, modelPartValue);
      handleArgbColor(nativeImage, 16, 16, 24, 16, argbColorValue, argbColorValue2);
      handleArgbColor(nativeImage, 20, 20, 8, 12, argbColorValue);
      handleArgbColor(nativeImage, 23, 20, 2, 6, modelPartValue);
      handleArgbColor(nativeImage, 16, 26, 24, 2, argbColorValue8);
      handleArgbColor(nativeImage, 22, 26, 4, 2, argbColorValue9);
      nativeImage.setColorArgb(23, 26, -4684242);
      nativeImage.setColorArgb(24, 26, -4684242);
      handleArgbColor(nativeImage, 16, 30, 24, 2, modelPartValue);
      handleArgbColor(nativeImage, 23, 36, 2, 6, modelPartValue);
      handleArgbColor(nativeImage, 20, 46, 8, 2, modelPartValue);

      for (int[] argbColorValues : new int[][]{{40, 16}, {32, 48}}) {
         handleArgbColor(nativeImage, argbColorValues[0], argbColorValues[1], 16, 16, argbColorValue, argbColorValue2);
         handleArgbColor(nativeImage, argbColorValues[0], argbColorValues[1] + 12, 16, 2, modelPartValue);
         handleArgbColor(nativeImage, argbColorValues[0], argbColorValues[1] + 14, 16, 2, argbColorValue8);
         handleArgbColor(nativeImage, argbColorValues[0] + 8, argbColorValues[1], 4, 4, argbColorValue8);
      }

      for (int[] argbColorValues2 : new int[][]{{0, 16}, {16, 48}}) {
         handleArgbColor(nativeImage, argbColorValues2[0], argbColorValues2[1], 16, 16, argbColorValue, argbColorValue2);
         handleArgbColor(nativeImage, argbColorValues2[0], argbColorValues2[1] + 12, 16, 4, argbColorValue8);
         handleArgbColor(nativeImage, argbColorValues2[0] + 8, argbColorValues2[1], 4, 4, argbColorValue8);
      }

      handleArgbColor(nativeImage, 4, 44, 4, 1, modelPartValue);
      handleArgbColor(nativeImage, 4, 60, 4, 1, modelPartValue);
      return nativeImage;
   }

   private static NativeImage getArgbColor6() {
      NativeImage nativeImage = new NativeImage(128, 64, true);
      handleArgbColor(nativeImage, 0, 0, 128, 64, -3924434, -7465952);

      for (int i = 0; i < 64; i++) {
         nativeImage.setColorArgb(i, 6, -1525686);
         nativeImage.setColorArgb(i, 15, -1525686);
      }

      handleArgbColor(nativeImage, 64, 0, 32, 12, -13730481, -14788040);
      handleArgbColor(nativeImage, 64, 12, 28, 14, -7710157, -9747418);
      handleArgbColor(nativeImage, 0, 40, 64, 10, -1525686, -4684242);
      handleArgbColor(nativeImage, 8, 44, 32, 16, -4684242, -7445472);
      return nativeImage;
   }

   private static NativeImage getArgbColor7() {
      NativeImage nativeImage = new NativeImage(32, 32, true);

      for (int i = 0; i < 32; i++) {
         for (int j = 0; j < 32; j++) {
            float argbColorFactor = (j + 0.5F) / 16.0F - 1.0F;
            float argbColorFactor2 = (i + 0.5F) / 16.0F - 1.0F;
            float argbColorValue = Math.min(1.0F, (float)Math.sqrt(argbColorFactor * argbColorFactor + argbColorFactor2 * argbColorFactor2));
            float argbColorFactor3 = (1.0F - argbColorValue) * (1.0F - argbColorValue);
            int round = Math.round(255.0F * argbColorFactor3 * argbColorFactor3 * (3.0F - 2.0F * argbColorFactor3));
            nativeImage.setColorArgb(j, i, round << 24 | 16777215);
         }
      }

      return nativeImage;
   }

   static ModelPart getModel9() {
      ModelData modelData = new ModelData();
      ModelPartData root = modelData.getRoot();
      root.addChild("body", ModelPartBuilder.create().uv(0, 9).cuboid(-3.0F, 0.0F, -1.5F, 6.0F, 6.0F, 3.0F), ModelTransform.origin(0.0F, -6.0F, 0.0F));
      root.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -6.0F, -1.5F, 6.0F, 6.0F, 3.0F), ModelTransform.origin(0.0F, -6.0F, 0.0F));
      root.addChild("arm_r", ModelPartBuilder.create().uv(18, 0).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F), ModelTransform.origin(-4.0F, -5.0F, 0.0F));
      root.addChild(
         "arm_l", ModelPartBuilder.create().uv(18, 0).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F), ModelTransform.origin(4.0F, -5.0F, 0.0F)
      );
      root.addChild("leg_r", ModelPartBuilder.create().uv(18, 9).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F), ModelTransform.origin(-1.6F, 0.0F, 0.0F));
      root.addChild(
         "leg_l", ModelPartBuilder.create().uv(18, 9).mirrored().cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F), ModelTransform.origin(1.6F, 0.0F, 0.0F)
      );
      return TexturedModelData.of(modelData, 32, 32).createModel();
   }
}
