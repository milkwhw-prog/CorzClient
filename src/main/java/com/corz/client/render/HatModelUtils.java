package com.corz.client.render;

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

public final class HatModelUtils {
   private static boolean textureIdentifierActive;
   static final Identifier identifier = Identifier.of("corzclient", "dynamic/santa_hat");

   private static void handleArgbColor(NativeImage nativeImage, int argbColorValue, int argbColorValue2, int argbColorValue3, int argbColorValue4, int hatModelValue, int argbColorValue6) {
      for (int i = argbColorValue2; i < argbColorValue2 + argbColorValue4; i++) {
         for (int j = argbColorValue; j < argbColorValue + argbColorValue3; j++) {
            int argbColorValue7 = j * 73856093 ^ i * 19349663;
            int argbColorValue8 = (argbColorValue7 ^ argbColorValue7 >>> 13) * 1540483477;
            float hatModelFactor = (argbColorValue8 >>> 8 & 0xFF) / 255.0F * 0.35F;
            nativeImage.setColorArgb(j, i, getValue(hatModelValue, argbColorValue6, hatModelFactor));
         }
      }
   }

   private static int getValue(int hatModelValue, int hatModelValue2, float hatModelFactor) {
      int hatModelValue3 = (int)((hatModelValue >> 16 & 0xFF) + ((hatModelValue2 >> 16 & 0xFF) - (hatModelValue >> 16 & 0xFF)) * hatModelFactor);
      int hatModelValue4 = (int)((hatModelValue >> 8 & 0xFF) + ((hatModelValue2 >> 8 & 0xFF) - (hatModelValue >> 8 & 0xFF)) * hatModelFactor);
      int hatModelValue5 = (int)((hatModelValue & 0xFF) + ((hatModelValue2 & 0xFF) - (hatModelValue & 0xFF)) * hatModelFactor);
      return 0xFF000000 | hatModelValue3 << 16 | hatModelValue4 << 8 | hatModelValue5;
   }

   public static ModelPart getChild() {
      ModelData modelData = new ModelData();
      ModelPartData root = modelData.getRoot();
      ModelPartData addChild = root.addChild(
         "hat",
         ModelPartBuilder.create().uv(0, 0).cuboid(-4.5F, -10.75F, -4.5F, 9.0F, 2.0F, 9.0F).uv(0, 11).cuboid(-4.0F, -13.5F, -4.0F, 8.0F, 3.0F, 8.0F),
         ModelTransform.NONE
      );
      ModelPartData addChild2 = addChild.addChild(
         "mid", ModelPartBuilder.create().uv(0, 22).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 3.0F, 6.0F), ModelTransform.of(0.4F, -13.2F, 0.0F, 0.0F, 0.0F, 0.22F)
      );
      ModelPartData addChild3 = addChild2.addChild(
         "tip", ModelPartBuilder.create().uv(0, 31).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F), ModelTransform.of(0.6F, -2.7F, 0.0F, 0.0F, 0.0F, 0.48F)
      );
      addChild3.addChild(
         "pom", ModelPartBuilder.create().uv(40, 0).cuboid(-1.5F, -2.6F, -1.5F, 3.0F, 3.0F, 3.0F), ModelTransform.of(0.5F, -2.5F, 0.0F, 0.0F, 0.0F, 0.35F)
      );
      return TexturedModelData.of(modelData, 64, 64).createModel().getChild("hat");
   }

   private HatModelUtils() {
   }

   public static Identifier getTextureIdentifier(MinecraftClient client) {
      if (!textureIdentifierActive) {
         NativeImage nativeImage = new NativeImage(64, 64, true);
         handleArgbColor(nativeImage, 0, 0, 36, 11, -724500, -2501428);
         handleArgbColor(nativeImage, 40, 0, 12, 6, -724500, -2501428);
         handleArgbColor(nativeImage, 0, 11, 32, 11, -3924434, -6679518);
         handleArgbColor(nativeImage, 0, 22, 24, 9, -3924434, -6679518);
         handleArgbColor(nativeImage, 0, 31, 16, 7, -3924434, -6679518);
         client.getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(() -> "corz santa hat", nativeImage));
         textureIdentifierActive = true;
      }

      return identifier;
   }
}
