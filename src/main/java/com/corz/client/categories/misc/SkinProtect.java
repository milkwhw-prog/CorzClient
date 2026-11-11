package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.RotateQuaternionfApi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo.TextureAsset;
import net.minecraft.util.Identifier;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.QuaternionfRotateCorzclient;
import com.corz.client.utils.IconsPngType;

public class SkinProtect extends Module implements RotateQuaternionfApi {
   private static final Map<Identifier, SkinTextures> CORZCLIENT_PONK_MAP;
   private final ButtonBase buttonBase;
   public static int corzclientValue;
   private static final List<Identifier> TECHNOBLADE_CORZCLIENT_ITEMS = new ArrayList<>();
   public final QuaternionfRotateCorzclient quaternionfRotateQuaternionf;
   private int skinShuffleValue = getNextInt();

   private void updateShuffleSkin() {
      if (TECHNOBLADE_CORZCLIENT_ITEMS.size() > 1) {
         int skinShuffleValue = this.skinShuffleValue;

         while (this.skinShuffleValue == skinShuffleValue) {
            this.skinShuffleValue = getNextInt();
         }
      }
   }

   private SkinTextures getIfAbsent(int ifAbsentValue) {
      Identifier identifier = TECHNOBLADE_CORZCLIENT_ITEMS.get(Math.floorMod(ifAbsentValue, TECHNOBLADE_CORZCLIENT_ITEMS.size()));
      return CORZCLIENT_PONK_MAP.computeIfAbsent(identifier, id -> SkinTextures.create(new SkinProtect.SkinAsset(id), null, null, PlayerSkinType.WIDE));
   }

   static {
      for (String skinProtectText : new String[]{
         "technoblade",
         "dream",
         "georgenotfound",
         "sapnap",
         "skeppy",
         "fundy",
         "purpled",
         "ph1lza",
         "wilbursoot",
         "tommyinnit",
         "foolish_gamers",
         "grian",
         "mumbojumbo",
         "iskall85",
         "xisumavoid",
         "zombiecleo",
         "docm77",
         "impulsesv",
         "bdoubleo100",
         "falsesymmetry",
         "cubfan135",
         "geminitay",
         "smajor1995",
         "etho",
         "tubbo",
         "quackity",
         "captainpuffy",
         "nihachu",
         "eret",
         "hbomb94",
         "vurb",
         "slimecicle",
         "ponk"
      }) {
         TECHNOBLADE_CORZCLIENT_ITEMS.add(Identifier.of("corzclient", "textures/skinprotect/" + skinProtectText + ".png"));
      }

      CORZCLIENT_PONK_MAP = new HashMap<>();
      corzclientValue = -1;
   }

   @Override
   public void updateQuaternionfRotate() {
      this.quaternionfRotateQuaternionf.updateFlag();
   }

   @Override
   public void renderQuaternionfRotate(DrawContext context, int quaternionfRotateValue, int editValue, int releaseRightLimit, int n4) {
      this.quaternionfRotateQuaternionf.renderCorzclientScale(context, quaternionfRotateValue, editValue, releaseRightLimit, n4, this.skinShuffleValue);
   }

   public int getSkinShuffleValue() {
      return this.skinShuffleValue;
   }

   @Override
   public void handleQuaternionfRotate(double quaternionfRotateValue, double quaternionfRotateValue2) {
      this.quaternionfRotateQuaternionf.handleAmountFlag(quaternionfRotateValue, quaternionfRotateValue2);
   }

   public SkinTextures getShuffleSkin() {
      return this.getIfAbsent(this.skinShuffleValue);
   }

   public static SkinTextures getCorzclientInstance(AbstractClientPlayerEntity abstractClientPlayerEntity) {
      if (MinecraftClient.getInstance().player != abstractClientPlayerEntity) {
         return null;
      } else if (ModuleRegistry.getModule(SkinProtect.class) instanceof SkinProtect skinProtect) {
         if (corzclientValue >= 0) {
            return skinProtect.getIfAbsent(corzclientValue);
         } else {
            return !skinProtect.isEnabled() ? null : skinProtect.getShuffleSkin();
         }
      } else {
         return null;
      }
   }

   private static int getNextInt() {
      return TECHNOBLADE_CORZCLIENT_ITEMS.isEmpty() ? 0 : ThreadLocalRandom.current().nextInt(TECHNOBLADE_CORZCLIENT_ITEMS.size());
   }

   @Override
   protected void onEnable() {
      this.skinShuffleValue = getNextInt();
   }

   public SkinProtect() {
      super("SkinProtect", IconsPngType.MISC);
      this.buttonBase = new ButtonBase("Shuffle Skin", "Roll a new random trendy skin", "Shuffle", this::updateShuffleSkin);
      this.quaternionfRotateQuaternionf = new QuaternionfRotateCorzclient();
      this.addSettings(new ConfigSetting[]{this.buttonBase});
   }

   @Override
   public boolean canQuaternionfRotate(double quaternionfRotateValue, double quaternionfRotateValue2, int quaternionfRotateValue3, int quaternionfRotateValue4, int quaternionfRotateValue5, int quaternionfRotateValue6, int quaternionfRotateValue7) {
      return this.quaternionfRotateQuaternionf.canAmountFlag(quaternionfRotateValue, quaternionfRotateValue2, quaternionfRotateValue3, quaternionfRotateValue4, quaternionfRotateValue5, quaternionfRotateValue6, quaternionfRotateValue7);
   }

   private record SkinAsset(Identifier tex) implements TextureAsset {
      @Override
      public Identifier id() {
         return this.tex;
      }

      @Override
      public Identifier texturePath() {
         return this.tex;
      }
   }
}
