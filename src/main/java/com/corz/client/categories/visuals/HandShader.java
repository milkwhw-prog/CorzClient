package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.render.MatrixEspUtils;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import com.corz.client.utils.AttachmentFramebuffer;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;

public class HandShader extends Module {
   private final DoubleSetting doubleHelper;
   private final BooleanSetting booleanBase = new BooleanSetting("Fire Effect", "Procedural flame trail on the hand", true);
   private final DoubleSetting trailBlur;
   private final DoubleSetting length;
   private final ColorSetting colorBase;
   private final DoubleSetting handBlur;
   private final BooleanSetting themeColor;
   private final DoubleSetting glowPower;
   private final DoubleSetting outlineAlpha;
   private final DoubleSetting auraWidth;
   private final DoubleSetting trailSoftness;
   private final BooleanSetting rainbowOutline;
   private final BooleanSetting itemColor;
   private final DoubleSetting intensitySmoothSolid = new DoubleSetting("Intensity", "Flame intensity", 0.8, 0.1, 1.5, 0.1);
   private final BooleanSetting customOutlineColor;
   private final BooleanSetting enableOutline;
   private final DoubleSetting speedSmoothSolid = new DoubleSetting("Speed", "Flame speed", 1.0, 0.2, 3.0, 0.1);
   private final EnumSetting enumHelper;
   private final DoubleSetting trailFade;
   private final DoubleSetting smokeSmoothSolid;
   private final DoubleSetting handFade;

   public HandShader() {
      super("HandShader", IconsPngType.VISUAL);
      this.length = new DoubleSetting("Length", "Flame height", 0.55, 0.1, 1.0, 0.01);
      this.smokeSmoothSolid = new DoubleSetting("Smoke", "Smoke amount", 0.55, 0.0, 0.8, 0.01);
      this.itemColor = new BooleanSetting("Item Color", "Tint from the held item", true);
      this.themeColor = new BooleanSetting("Theme Color", "Tint from the client theme (when Item Color is off)", true);
      this.trailFade = new DoubleSetting("Trail Fade", "Trail fade", 0.84, 0.55, 0.96, 0.01);
      this.trailSoftness = new DoubleSetting("Trail Softness", "Trail softness", 1.35, 0.45, 2.5, 0.01);
      this.trailBlur = new DoubleSetting("Trail Blur", "Trail blur radius", 1.55, 0.2, 3.0, 0.01);
      this.handFade = new DoubleSetting("Hand Fade", "Hand fade", 0.68, 0.45, 0.9, 0.01);
      this.doubleHelper = new DoubleSetting("Hand Softness", "Hand softness", 1.3, 0.45, 2.5, 0.1);
      this.handBlur = new DoubleSetting("Hand Blur", "Hand blur radius", 1.4, 0.2, 3.0, 0.1);
      this.enableOutline = new BooleanSetting("Enable Outline", "Glow outline around the hand/item", false);
      this.enumHelper = new EnumSetting("Style", "Outline style", "Glow Aura", "Glow Aura", "Smooth", "Solid");
      this.auraWidth = new DoubleSetting("Aura Width", "Outline width", 3.0, 1.0, 8.0, 0.1);
      this.glowPower = new DoubleSetting("Glow Power", "Outline glow power", 1.8, 0.2, 4.0, 0.1);
      this.outlineAlpha = new DoubleSetting("Outline Alpha", "Outline opacity", 0.85, 0.1, 1.0, 0.05);
      this.customOutlineColor = new BooleanSetting("Custom Outline Color", "Use a custom outline colour", false);
      this.colorBase = new ColorSetting("Outline Color", "Custom outline colour", -6275841);
      this.rainbowOutline = new BooleanSetting("Rainbow Outline", "Cycle the outline hue", false);
      this.addSettings(
         new ConfigSetting[]{
            this.booleanBase,
            this.intensitySmoothSolid,
            this.speedSmoothSolid,
            this.length,
            this.smokeSmoothSolid,
            this.itemColor,
            this.themeColor,
            this.trailFade,
            this.trailSoftness,
            this.trailBlur,
            this.handFade,
            this.doubleHelper,
            this.handBlur,
            this.enableOutline,
            this.enumHelper,
            this.auraWidth,
            this.glowPower,
            this.outlineAlpha,
            this.customOutlineColor,
            this.colorBase,
            this.rainbowOutline
         }
      );
   }

   @Override
   protected void onDisable() {
      AttachmentFramebuffer.getInstance().handleTrailSampler(false);
   }

   public static HandShader getHandShader() {
      return (HandShader)ModuleRegistry.getModule(HandShader.class);
   }

   @Override
   protected void onEnable() {
      this.updateSmoothSolid();
   }

   private void updateSmoothSolid() {
      AttachmentFramebuffer instance = AttachmentFramebuffer.getInstance();
      boolean smoothSolidFlag = this.isEnabled() && (this.booleanBase.isEnabled() || this.enableOutline.isEnabled());
      instance.handleTrailSampler(smoothSolidFlag);
      if (smoothSolidFlag) {
         instance.setFlagPostActive(this.booleanBase.isEnabled());
         instance.setAmountTrailGlowStrength((float)this.intensitySmoothSolid.getValue());
         instance.setAmountGlowFalloff(0.35F);
         instance.setAmountFireSaturation((float)this.speedSmoothSolid.getValue());
         instance.setAmountFireBrightness((float)this.length.getValue());
         instance.setAmountTrailDecay((float)this.trailFade.getValue());
         instance.setAmountTrailBrightness((float)this.trailSoftness.getValue());
         instance.setAmountFireContrast((float)this.trailBlur.getValue());
         instance.setAmountIntensityDecay((float)this.handFade.getValue());
         instance.setAmountPostAmount((float)this.doubleHelper.getValue());
         instance.setAmountForFloat((float)this.handBlur.getValue());
         instance.setAmountFireThreshold((float)this.smokeSmoothSolid.getValue());
         Color baseRelease = this.getBaseRelease();
         instance.handleAmount(baseRelease.getRed() / 255.0F, baseRelease.getGreen() / 255.0F, baseRelease.getBlue() / 255.0F, 1.0F);
         instance.setFlagForFlag(this.enableOutline.isEnabled());
         instance.setAmountTrailWidth((float)this.auraWidth.getValue());
         instance.setAmountFireAlpha((float)this.glowPower.getValue());
         String formatText = this.enumHelper.getCurrentOption();

         byte smoothSolidValue = switch (formatText) {
            case "Solid" -> 0;
            case "Smooth" -> 2;
            default -> 1;
         };
         instance.setValue(smoothSolidValue);
         Color hsbcolor = this.getHsbcolor();
         instance.handleAmountForFloat(
            hsbcolor.getRed() / 255.0F, hsbcolor.getGreen() / 255.0F, hsbcolor.getBlue() / 255.0F, hsbcolor.getAlpha() / 255.0F * (float)this.outlineAlpha.getValue()
         );
      }
   }

   public Color getHsbcolor() {
      if (this.customOutlineColor.isEnabled()) {
         return new Color(this.colorBase.getColor(), true);
      } else if (this.rainbowOutline.isEnabled()) {
         float hsbcolorValue = (float)(System.currentTimeMillis() % 3000L) / 3000.0F;
         return Color.getHSBColor(hsbcolorValue, 0.8F, 1.0F);
      } else {
         return this.getBaseRelease();
      }
   }

   public Color getBaseRelease() {
      if (this.itemColor.isEnabled()) {
         return this.getWhite();
      } else {
         return this.themeColor.isEnabled() ? new Color(MatrixEspUtils.releaseRightValue, true) : new Color(6697983);
      }
   }

   @Override
   public void onReset() {
      this.updateSmoothSolid();
   }

   public Color getWhite() {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player == null) {
         return Color.WHITE;
      } else {
         ItemStack mainHandStack = instance.player.getMainHandStack();
         if (mainHandStack.isEmpty()) {
            mainHandStack = instance.player.getOffHandStack();
         }

         if (mainHandStack.isEmpty()) {
            return Color.WHITE;
         } else {
            String path = Registries.ITEM.getId(mainHandStack.getItem()).getPath();
            if (path.contains("diamond")) {
               return new Color(5627360);
            } else if (path.contains("netherite")) {
               return new Color(5982823);
            } else if (path.contains("gold")) {
               return new Color(16766042);
            } else if (path.contains("iron")) {
               return new Color(14212840);
            } else if (path.contains("emerald")) {
               return new Color(3526767);
            } else if (path.contains("redstone")) {
               return new Color(14826299);
            } else {
               return path.contains("lapis") ? new Color(3233492) : new Color(15132390);
            }
         }
      }
   }
}
