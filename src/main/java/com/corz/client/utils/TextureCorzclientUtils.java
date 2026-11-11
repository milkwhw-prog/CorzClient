package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.CorzclientWarm;

public final class TextureCorzclientUtils {
   private static final Map<String, Identifier> CORZCLIENT_MAP = new HashMap<>();

   public static Identifier getCorzclientTexture(WarmBrainrotBase warmBrainrotBase, CorzclientWarm corzclientWarm) {
      Identifier corzclient_map = CORZCLIENT_MAP.get(warmBrainrotBase.formatTungDin());
      if (corzclient_map != null) {
         return corzclient_map;
      } else {
         Identifier corzclientTexture = Identifier.of("corzclient", "brainrot/" + warmBrainrotBase.formatTungDin());
         NativeImage nativeImage = new NativeImage(corzclientWarm.corzclientValue, corzclientWarm.corzclientTextureValue, false);
         int[] corzclientValues = corzclientWarm.corzclientValues;

         for (int i = 0; i < corzclientWarm.corzclientTextureValue; i++) {
            for (int j = 0; j < corzclientWarm.corzclientValue; j++) {
               nativeImage.setColorArgb(j, i, corzclientValues[i * corzclientWarm.corzclientValue + j]);
            }
         }

         String corzclientTextureText = "corz_brainrot_" + warmBrainrotBase.formatTungDin();
         MinecraftClient.getInstance().getTextureManager().registerTexture(corzclientTexture, new NativeImageBackedTexture(() -> corzclientTextureText, nativeImage));
         CORZCLIENT_MAP.put(warmBrainrotBase.formatTungDin(), corzclientTexture);
         return corzclientTexture;
      }
   }

   private TextureCorzclientUtils() {
   }
}
