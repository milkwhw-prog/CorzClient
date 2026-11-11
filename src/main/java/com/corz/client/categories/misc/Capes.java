package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.gui.RotateQuaternionfApi;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.AssetInfo.TextureAsset;
import net.minecraft.util.Identifier;
import com.corz.client.utils.QuaternionfRotateIdentifier;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;

public class Capes extends Module implements RotateQuaternionfApi {
   private static final String CORZCLIENT = "corzclient";
   private final EnumSetting enumHelper = new EnumSetting("Selected Cape", "Which bundled cape to wear (None = off)", "None", getClassicNone());
   private static final Map<Identifier, Capes.CapeAsset> CAKE_MOJANG_MAP = new HashMap<>();
   public static Identifier identifier = null;
   public final QuaternionfRotateIdentifier quaternionfRotate;
   private static Capes singleton;
   private final BooleanSetting booleanBase = new BooleanSetting("Show On Self", "Render the cape on your own player", true);
   private static final String TEXTURES_CAPES = "textures/capes/";
   public static final String NONE = "None";
   private static final Map<String, Identifier> CORZ_CLASSIC_MAP = new LinkedHashMap<>();
   private final BooleanSetting capesBooleanBase = new BooleanSetting("3D Cape", "A real cloth cape that moves with you. Off uses the flat vanilla cape", true);

   public Capes() {
      super("Capes Library", IconsPngType.MISC);
      this.quaternionfRotate = new QuaternionfRotateIdentifier();
      this.addSettings(new ConfigSetting[]{this.enumHelper, this.booleanBase, this.capesBooleanBase});
      singleton = this;
   }

   public static TextureAsset getIfAbsent(Identifier id) {
      return CAKE_MOJANG_MAP.computeIfAbsent(id, Capes.CapeAsset::new);
   }

   public Identifier getNoneClassic() {
      String formatText = this.enumHelper.getCurrentOption();
      return "None".equals(formatText) ? null : CORZ_CLASSIC_MAP.get(formatText);
   }

   public static boolean isBaseInstance() {
      Capes singleton2 = singleton;
      return singleton2 == null || singleton2.capesBooleanBase.isEnabled();
   }

   @Override
   public void handleQuaternionfRotate(double quaternionfRotateValue, double quaternionfRotateValue2) {
      this.quaternionfRotate.handleAmountMin(quaternionfRotateValue, quaternionfRotateValue2);
   }

   @Override
   public void updateQuaternionfRotate() {
      this.quaternionfRotate.updateFlagMin();
   }

   public static Identifier getIdentifierInstance(AbstractClientPlayerEntity abstractClientPlayerEntity) {
      if (MinecraftClient.getInstance().player != abstractClientPlayerEntity) {
         return null;
      } else if (identifier != null) {
         return identifier;
      } else if (!(ModuleRegistry.getModule(Capes.class) instanceof Capes capes && capes.isEnabled())) {
         return null;
      } else {
         return !capes.booleanBase.isEnabled() ? null : capes.getNoneClassic();
      }
   }

   public Identifier getIdentifier() {
      return this.getNoneClassic();
   }

   private static Identifier getIdentifier(String identifierText) {
      return Identifier.of("corzclient", "textures/capes/" + identifierText + ".png");
   }

   static {
      CORZ_CLASSIC_MAP.put("CORZ V1 ", getIdentifier("obsidian_silver"));
      CORZ_CLASSIC_MAP.put("CORZ V2", getIdentifier("carbon_gold"));
      CORZ_CLASSIC_MAP.put("CORZ V3 CLASSIC", getIdentifier("midnight_cobalt"));
      CORZ_CLASSIC_MAP.put("CORZ V4", getIdentifier("crimson_core"));
      CORZ_CLASSIC_MAP.put("CORZ V5", getIdentifier("void_amethyst"));
      CORZ_CLASSIC_MAP.put("GreenEven", getIdentifier("green_even"));
      CORZ_CLASSIC_MAP.put("Minecon 2013", getIdentifier("minecon_2013"));
      CORZ_CLASSIC_MAP.put("IronGolem", getIdentifier("iron_golem"));
      CORZ_CLASSIC_MAP.put("Mojang Classic", getIdentifier("mojang_classic"));
      CORZ_CLASSIC_MAP.put("Cake", getIdentifier("birthday_cake"));
   }

   public static boolean canCapesTextures(Identifier id) {
      return id != null && "corzclient".equals(id.getNamespace()) && id.getPath().startsWith("textures/capes/");
   }

   @Override
   public void renderQuaternionfRotate(DrawContext context, int quaternionfRotateValue, int matrixStackValue, int quaternionfRotateValue3, int materialFactor) {
      this.quaternionfRotate.renderIdentifierScale(context, quaternionfRotateValue, matrixStackValue, quaternionfRotateValue3, materialFactor, this.getIdentifier());
   }

   @Override
   public boolean canQuaternionfRotate(double quaternionfRotateValue, double quaternionfRotateValue2, int quaternionfRotateValue3, int quaternionfRotateValue4, int quaternionfRotateValue5, int quaternionfRotateValue6, int quaternionfRotateValue7) {
      return this.quaternionfRotate.canAmountMin(quaternionfRotateValue, quaternionfRotateValue2, quaternionfRotateValue3, quaternionfRotateValue4, quaternionfRotateValue5, quaternionfRotateValue6, quaternionfRotateValue7);
   }

   private static String[] getClassicNone() {
      String[] strings = new String[CORZ_CLASSIC_MAP.size() + 1];
      strings[0] = "None";
      int classicNoneCounter = 1;

      for (String classicNoneText : CORZ_CLASSIC_MAP.keySet()) {
         strings[classicNoneCounter++] = classicNoneText;
      }

      return strings;
   }

   private record CapeAsset(Identifier tex) implements TextureAsset {
      @Override
      public Identifier texturePath() {
         return this.tex;
      }

      @Override
      public Identifier id() {
         return this.tex;
      }
   }
}
