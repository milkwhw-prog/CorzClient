package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.gui.TickProgress;
import com.corz.client.gui.VertexGuiUtils;
import com.corz.client.render.FlameCorzUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.utils.MinimalSharpUtils.FailureGate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.MinimalSharpUtils;

public class CustomFlame extends Module {
   private final ColorSetting colorBase;
   private final ColorSetting flameColor = new ColorSetting("Flame Color", "Primary flame colour (alpha is respected)", -42464);
   private final DoubleSetting doubleHelper;
   private final DoubleSetting edgeGlow;
   private final DoubleSetting centerClear;
   private final BooleanSetting booleanBase;
   private static final float[][] customFlameFloats = new float[][]{{0.7071F, 0.7071F}, {0.7071F, -0.7071F}};
   private final DoubleSetting animationSpeed;
   private final int[] customFlameTable;
   private int worldFireValue;
   private int reloadCooldownTicks;
   private long fireWorldTime;
   private final long lastNanoTime;
   private final DoubleSetting worldFlameHeight;
   private final FailureGate fireWorldFailureGate;
   private float worldFireAmount;
   private final List<BlockPos> blockImmutableBlockPosList;
   private final DoubleSetting flameHeight;
   private final BooleanSetting hideVanillaFire;
   private final DoubleSetting opacityFireWorld;
   private boolean heightWorldFlag;
   private final EnumSetting enumHelper;
   private final DoubleSetting bottomFade;
   private final float[] table;
   private boolean reloadRendererActive;
   private final BooleanSetting restyleWorldFire;
   private final long unusedTimeNanos;

   private void handleAmounts(
      VertexConsumer vertices,
      Matrix4f matrix,
      float customFlameValue,
      float customFlameValue2,
      float customFlameValue3,
      float customFlameFactor,
      float customFlameFactor2,
      float customFlameValue4,
      float customFlameValue5,
      int minimalSharp,
      float customFlameValue7,
      float customFlameValue8,
      int[] customFlameValues
   ) {
      float[] table2 = this.table;

      for (int i = 0; i <= 14; i++) {
         table2[i] = getAmount(i / 14.0F, customFlameValue4, customFlameValue5, minimalSharp, customFlameValue7, customFlameValue8);
      }

      for (int j = 0; j < 14; j++) {
         float customFlameValue9 = j / 14.0F;
         float customFlameValue10 = (j + 1) / 14.0F;
         float customFlameFactor3 = table2[j];
         float customFlameFactor4 = table2[j + 1];
         float customFlameFactor5 = (customFlameValue9 - 0.5F) * 2.0F * 0.6F;
         float customFlameFactor6 = (customFlameValue10 - 0.5F) * 2.0F * 0.6F;
         float vertexColorValue = customFlameValue + customFlameFactor * customFlameFactor5;
         float amountForDouble = customFlameValue3 + customFlameFactor2 * customFlameFactor5;
         float amountX = customFlameValue + customFlameFactor * customFlameFactor6;
         float customFlameValue14 = customFlameValue3 + customFlameFactor2 * customFlameFactor6;

         for (int k = 0; k < 3; k++) {
            float customFlameFactor7 = k / 3.0F;
            float customFlameFactor8 = (k + 1) / 3.0F;
            int customFlameValue15 = customFlameValues[j * 4 + k];
            int customFlameValue16 = customFlameValues[(j + 1) * 4 + k];
            int customFlameValue17 = customFlameValues[(j + 1) * 4 + k + 1];
            int valueInt = customFlameValues[j * 4 + k + 1];
            VertexBuilderUtils.handleVertexColor(
               vertices,
               matrix,
               vertexColorValue,
               customFlameValue2 + customFlameFactor7 * customFlameFactor3,
               amountForDouble,
               customFlameValue15,
               amountX,
               customFlameValue2 + customFlameFactor7 * customFlameFactor4,
               customFlameValue14,
               customFlameValue16,
               amountX,
               customFlameValue2 + customFlameFactor8 * customFlameFactor4,
               customFlameValue14,
               customFlameValue17,
               vertexColorValue,
               customFlameValue2 + customFlameFactor8 * customFlameFactor3,
               amountForDouble,
               valueInt
            );
         }
      }
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      if (isBaseRestyleWorldFire() && mc.world != null && mc.player != null) {
         if (!this.blockImmutableBlockPosList.isEmpty()) {
            Camera camera = mc.gameRenderer.getCamera();
            if (camera != null) {
               Vec3d cameraPos = camera.getCameraPos();
               float customFlameValue = (float)((System.nanoTime() - this.lastNanoTime) / 1.0E9 % 3600.0) * MinimalSharpUtils.getAmount6(this.animationSpeed.getValue());
               int minimalSharp = MinimalSharpUtils.getMinimalSharp(this.enumHelper.getCurrentOption());
               float amountForDouble = MinimalSharpUtils.getAmountForDouble(this.doubleHelper.getValue());
               float amount4 = MinimalSharpUtils.getAmount4(this.opacityFireWorld.getValue());
               float customFlameFactor = (float)this.worldFlameHeight.getValue();
               int intValue = this.flameColor.getColor();
               int customFlameValue5 = this.booleanBase.isEnabled() ? MinimalSharpUtils.getValue(intValue) : this.colorBase.getColor();
               int[] customFlameValues = this.customFlameTable;

               for (int i = 0; i <= 14; i++) {
                  for (int j = 0; j <= 3; j++) {
                     customFlameValues[i * 4 + j] = getValue(i / 14.0F, j / 3.0F, intValue, customFlameValue5, amount4);
                  }
               }

               queueCommand.matrices.push();
               queueCommand.matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
               Matrix4f positionMatrix = queueCommand.matrices.peek().getPositionMatrix();
               VertexConsumer buffer = VertexBuilderUtils.getBuffer();

               for (BlockPos blockPos : this.blockImmutableBlockPosList) {
                  this.handleAmounts(buffer, positionMatrix, blockPos, customFlameValue, minimalSharp, amountForDouble, customFlameFactor, customFlameValues);
               }

               queueCommand.matrices.pop();
            }
         }
      }
   }

   @Override
   protected void onEnable() {
      this.updateReloadRenderer();
   }

   private void handleAmounts(VertexConsumer vertices, Matrix4f matrix, BlockPos pos2, float customFlameValue, int minimalSharp, float amountForDouble, float customFlameFactor, int[] customFlameValues) {
      float customFlameValue5 = pos2.getX() + 0.5F;
      float customFlameValue6 = pos2.getY() + 0.01F;
      float customFlameValue7 = pos2.getZ() + 0.5F;
      float customFlameValue8 = pos2.getX() * 0.37F + pos2.getZ() * 0.61F;

      for (int i = 0; i < customFlameFloats.length; i++) {
         this.handleAmounts(vertices, matrix, customFlameValue5, customFlameValue6, customFlameValue7, customFlameFloats[i][0], customFlameFloats[i][1], customFlameValue8 + i * 3.1F, customFlameValue, minimalSharp, amountForDouble, customFlameFactor, customFlameValues);
      }
   }

   public boolean isBase() {
      return this.hideVanillaFire.isEnabled();
   }

   private static float getAmount(float customFlameValue, float customFlameValue2, float customFlameValue3, int minimalSharp, float customFlameValue5, float customFlameFactor) {
      float customFlameValue6 = MinimalSharpUtils.getAmount(customFlameValue + customFlameValue2, customFlameValue3, minimalSharp, customFlameValue5);
      float customFlameFactor2 = 0.32F + 0.68F * customFlameValue6;
      float customFlameFactor3 = 0.6F + 0.4F * (1.0F - Math.abs(customFlameValue - 0.5F) * 2.0F);
      return customFlameFactor * customFlameFactor2 * customFlameFactor3;
   }

   private static int getValue(float customFlameValue, float customFlameValue2, int intValue, int customFlameValue4, float customFlameValue5) {
      float customFlameValue6 = 1.0F - Math.min(1.0F, Math.abs(customFlameValue - 0.5F) * 2.0F);
      float customFlameFactor = MinimalSharpUtils.getAmount((1.0F - customFlameValue2) * (0.35F + 0.65F * customFlameValue6), 0.0F, 1.0F);
      int customFlameValue7 = intValue >> 16 & 0xFF;
      int customFlameValue8 = intValue >> 8 & 0xFF;
      int customFlameValue9 = intValue & 0xFF;
      int customFlameValue10 = customFlameValue4 >> 16 & 0xFF;
      int customFlameValue11 = customFlameValue4 >> 8 & 0xFF;
      int customFlameValue12 = customFlameValue4 & 0xFF;
      int round = Math.round(customFlameValue7 + (customFlameValue10 - customFlameValue7) * customFlameFactor);
      int round14 = Math.round(customFlameValue8 + (customFlameValue11 - customFlameValue8) * customFlameFactor);
      int round15 = Math.round(customFlameValue9 + (customFlameValue12 - customFlameValue9) * customFlameFactor);
      float pow = (float)Math.pow(MinimalSharpUtils.getAmount(1.0F - customFlameValue2, 0.0F, 1.0F), 0.6);
      int customFlameBound = Math.round(255.0F * customFlameValue5 * pow);
      int customFlameValue16 = customFlameBound < 0 ? 0 : Math.min(customFlameBound, 255);
      return customFlameValue16 << 24 | round << 16 | round14 << 8 | round15;
   }

   @Override
   public void onRender2D(TickProgress tickProgress) {
      if (mc.player != null && mc.world != null) {
         long fireWorldTime = System.nanoTime();
         MinimalSharpUtils.isBoolean(() -> {
            MinimalSharpUtils.handle(() -> this.handleFireWorld(fireWorldTime), () -> this.fireWorldTime = fireWorldTime);
            return true;
         });
      }
   }

   private void handleFireWorld(long fireWorldValue) {
      boolean firstPerson = mc.options.getPerspective().isFirstPerson();
      boolean hasCurrentscreen = mc.currentScreen != null;
      boolean onFire = mc.player.isOnFire();
      double minimalSharpLimit = this.fireWorldTime == 0L ? 0.0 : (fireWorldValue - this.fireWorldTime) / 1.0E9;
      double fireWorldValue3 = MinimalSharpUtils.getAmount(minimalSharpLimit, 0.0, 0.1);
      boolean fireWorldFlag = onFire && firstPerson && !hasCurrentscreen;
      this.worldFireAmount = MinimalSharpUtils.getAmount(this.worldFireAmount, fireWorldFlag, fireWorldValue3);
      if (this.isEnabled() && this.hideVanillaFire.isEnabled() && firstPerson && onFire) {
         boolean fireWorldFlag2 = true;
      } else {
         boolean fireWorldFlag3 = false;
      }

      if (MinimalSharpUtils.canFlag(this.isEnabled(), firstPerson, hasCurrentscreen, this.worldFireAmount)) {
         int intValue = this.flameColor.getColor();
         int fireWorldValue4 = this.booleanBase.isEnabled() ? MinimalSharpUtils.getValue(intValue) : this.colorBase.getColor();
         float fireWorldValue5 = (intValue >> 16 & 0xFF) / 255.0F;
         float fireWorldValue6 = (intValue >> 8 & 0xFF) / 255.0F;
         float panelSamplerValue = (intValue & 0xFF) / 255.0F;
         float fireWorldValue8 = (intValue >>> 24 & 0xFF) / 255.0F;
         float fireWorldValue9 = (fireWorldValue4 >> 16 & 0xFF) / 255.0F;
         float fireWorldValue10 = (fireWorldValue4 >> 8 & 0xFF) / 255.0F;
         float fireWorldValue11 = (fireWorldValue4 & 0xFF) / 255.0F;
         float customFlameValue = MinimalSharpUtils.getAmount7(this.flameHeight.getValue());
         float amount4 = MinimalSharpUtils.getAmount4(this.opacityFireWorld.getValue());
         float amount6 = MinimalSharpUtils.getAmount6(this.animationSpeed.getValue());
         float amountForDouble = MinimalSharpUtils.getAmountForDouble(this.doubleHelper.getValue());
         float amountFloat = MinimalSharpUtils.getAmountFloat(this.edgeGlow.getValue());
         float amount5 = MinimalSharpUtils.getAmount5(this.centerClear.getValue());
         float fireWorldValue18 = MinimalSharpUtils.getAmount(this.bottomFade.getValue());
         int minimalSharp = MinimalSharpUtils.getMinimalSharp(this.enumHelper.getCurrentOption());
         double scaleFactor = mc.getWindow().getScaleFactor();
         float framebufferWidth = mc.getWindow().getFramebufferWidth();
         float framebufferHeight = mc.getWindow().getFramebufferHeight();
         if (!(framebufferWidth < 1.0F) && !(framebufferHeight < 1.0F)) {
            float customFlameFactor = (float)(framebufferWidth / scaleFactor);
            float flameCorzFactor = (float)(framebufferHeight / scaleFactor);
            float fireWorldValue21 = (float)((fireWorldValue - this.lastNanoTime) / 1.0E9 % 3600.0);
            float fireWorldValue22 = MinimalSharpUtils.getAmount(customFlameValue * 1.35F + 0.12F + amountFloat * 0.05F, 0.14F, 0.62F);
            long nanoTime = System.nanoTime();
            if (MinimalSharpUtils.canFlag(this.heightWorldFlag)) {
               this.handle(customFlameFactor, flameCorzFactor, customFlameValue, amount4, amount5, this.worldFireAmount, intValue);
               boolean fireWorldFlag4 = true;
            } else {
               boolean canFlag = FlameCorzUtils.canFlag(
                  customFlameFactor,
                  flameCorzFactor,
                  framebufferWidth,
                  framebufferHeight,
                  fireWorldValue5,
                  fireWorldValue6,
                  panelSamplerValue,
                  fireWorldValue8,
                  fireWorldValue9,
                  fireWorldValue10,
                  fireWorldValue11,
                  fireWorldValue21,
                  amount4,
                  customFlameValue,
                  amount6,
                  amountForDouble,
                  amountFloat,
                  amount5,
                  fireWorldValue18,
                  minimalSharp,
                  this.worldFireAmount,
                  fireWorldValue22
               );
               if (canFlag) {
                  this.worldFireValue = 0;
                  boolean fireWorldFlag6 = true;
               } else {
                  this.worldFireValue++;
                  if (MinimalSharpUtils.canFlag(this.worldFireValue)) {
                     this.heightWorldFlag = true;
                     if (this.fireWorldFailureGate.isFlagFailureGateActive()) {
                        System.err.println("[C");
                        System.err.flush();
                     }

                     this.handle(customFlameFactor, flameCorzFactor, customFlameValue, amount4, amount5, this.worldFireAmount, intValue);
                     boolean fireWorldFlag7 = true;
                  } else {
                     boolean fireWorldFlag8 = false;
                  }
               }
            }

            long fireWorldValue23 = (System.nanoTime() - nanoTime) / 1000L;
         }
      }
   }

   public CustomFlame() {
      super("Custom Flame", IconsPngType.VISUAL);
      this.colorBase = new ColorSetting("Secondary Color", "Inner core colour (used when Auto Secondary Color is off)", -15798);
      this.booleanBase = new BooleanSetting("Auto Secondary Color", "Derive a brighter inner core from Flame Color", true);
      this.flameHeight = new DoubleSetting("Flame Height", "Peak flame height as a fraction of screen height (well below vanilla)", 0.18, 0.08, 0.35, 0.01);
      this.opacityFireWorld = new DoubleSetting("Opacity", "Overall flame opacity", 0.55, 0.1, 1.0, 0.05);
      this.animationSpeed = new DoubleSetting("Animation Speed", "Flame movement speed", 1.0, 0.2, 2.5, 0.05);
      this.doubleHelper = new DoubleSetting("Flame Intensity", "Flame density / peak strength", 0.75, 0.2, 1.5, 0.05);
      this.edgeGlow = new DoubleSetting("Edge Glow", "Cheap local glow around the flame tips (no framebuffer bloom)", 0.2, 0.0, 1.0, 0.05);
      this.centerClear = new DoubleSetting("Center Clear", "How aggressively flames are reduced around the crosshair", 0.55, 0.2, 0.8, 0.05);
      this.bottomFade = new DoubleSetting("Bottom Fade", "Fade of the flame base into the screen edge", 0.35, 0.0, 1.0, 0.05);
      this.enumHelper = new EnumSetting("Style", "Flame look", "Native", MinimalSharpUtils.fireFlameTexts);
      this.hideVanillaFire = new BooleanSetting("Hide Vanilla Fire", "Replace the vanilla first-person fire overlay (off = draw in addition)", true);
      this.restyleWorldFire = new BooleanSetting("Restyle World Fire", "Replace world fire blocks with the same procedural flame (drawn in 3D)", true);
      this.worldFlameHeight = new DoubleSetting("World Flame Height", "Height of world fire flames, in blocks", 1.0, 0.3, 1.6, 0.1);
      this.lastNanoTime = System.nanoTime();
      this.fireWorldTime = 0L;
      this.worldFireAmount = 0.0F;
      this.worldFireValue = 0;
      this.heightWorldFlag = false;
      this.fireWorldFailureGate = new FailureGate();
      this.unusedTimeNanos = 0L;
      this.blockImmutableBlockPosList = new ArrayList<>();
      this.reloadCooldownTicks = 0;
      this.reloadRendererActive = false;
      this.customFlameTable = new int[60];
      this.table = new float[15];
      this.addSettings(
         new ConfigSetting[]{
            this.flameColor,
            this.colorBase,
            this.booleanBase,
            this.flameHeight,
            this.opacityFireWorld,
            this.animationSpeed,
            this.doubleHelper,
            this.edgeGlow,
            this.centerClear,
            this.bottomFade,
            this.enumHelper,
            this.hideVanillaFire,
            this.restyleWorldFire,
            this.worldFlameHeight
         }
      );
   }

   @Override
   public void onReset() {
      this.updateReloadRenderer();
      if (isBaseRestyleWorldFire() && mc.world != null && mc.player != null) {
         if (--this.reloadCooldownTicks <= 0) {
            this.reloadCooldownTicks = 6;
            this.updateBlockImmutable();
         }
      } else {
         if (!this.blockImmutableBlockPosList.isEmpty()) {
            this.blockImmutableBlockPosList.clear();
         }
      }
   }

   @Override
   protected void onDisable() {
      this.worldFireAmount = 0.0F;
      this.fireWorldTime = 0L;
      this.blockImmutableBlockPosList.clear();
      this.updateReloadRenderer();
   }

   private void handle(float customFlameFactor, float flameCorzFactor, float customFlameValue, float customFlameFactor3, float panelSamplerValue, float customFlameFactor4, int intValue) {
      byte customFlameValue4 = 6;
      float fadeScaled = customFlameValue * 0.9F;
      float customFlameFactor5 = flameCorzFactor * fadeScaled / 6.0F;
      float customFlameFactor6 = MinimalSharpUtils.getAmount(customFlameFactor3 * customFlameFactor4, 0.0F, 1.0F);
      int customFlameValue5 = intValue >> 16 & 0xFF;
      int customFlameValue6 = intValue >> 8 & 0xFF;
      int customFlameValue7 = intValue & 0xFF;
      float argbScaled = customFlameFactor * 0.5F;
      float customFlameValue8 = customFlameFactor * (0.1F + panelSamplerValue * 0.18F);
      VertexConsumer quads = VertexGuiUtils.getQuads();

      for (int i = 0; i < 6; i++) {
         float customFlameValue9 = flameCorzFactor - i * customFlameFactor5;
         float configsLikeValue = flameCorzFactor - (i + 1) * customFlameFactor5;
         int customFlameLimit = Math.round(customFlameFactor6 * (1.0F - i / 6.0F) * 255.0F);
         int customFlameValue11 = customFlameLimit < 0 ? 0 : (customFlameLimit > 255 ? 255 : customFlameLimit);
         int panelOverlay = customFlameValue11 << 24 | customFlameValue5 << 16 | customFlameValue6 << 8 | customFlameValue7;
         VertexGuiUtils.handleVertexColor(quads, 0.0F, configsLikeValue, argbScaled - customFlameValue8, customFlameValue9, panelOverlay);
         VertexGuiUtils.handleVertexColor(quads, argbScaled + customFlameValue8, configsLikeValue, customFlameFactor, customFlameValue9, panelOverlay);
      }

      VertexGuiUtils.updateBufferClose();
   }

   private void updateBlockImmutable() {
      this.blockImmutableBlockPosList.clear();
      BlockPos blockPos = mc.player.getBlockPos();
      byte blockImmutableValue = 18;
      byte blockImmutableValue2 = 8;
      short blockImmutableValue3 = 256;
      Mutable mutable = new Mutable();

      for (int i = -18; i <= 18; i++) {
         for (int j = -18; j <= 18; j++) {
            for (int k = -8; k <= 8; k++) {
               mutable.set(blockPos.getX() + i, blockPos.getY() + k, blockPos.getZ() + j);
               if (mc.world.getBlockState(mutable).getBlock() instanceof AbstractFireBlock) {
                  this.blockImmutableBlockPosList.add(mutable.toImmutable());
                  if (this.blockImmutableBlockPosList.size() >= 256) {
                     return;
                  }
               }
            }
         }
      }
   }

   private void updateReloadRenderer() {
      boolean flag = isBaseRestyleWorldFire();
      if (flag != this.reloadRendererActive) {
         this.reloadRendererActive = flag;
         if (mc.world != null && mc.worldRenderer != null) {
            mc.worldRenderer.reload();
         }
      }
   }

   public static boolean isBaseRestyleWorldFire() {
      CustomFlame customFlame = (CustomFlame)ModuleRegistry.getModule(CustomFlame.class);
      return customFlame != null && customFlame.isEnabled() && customFlame.restyleWorldFire.isEnabled();
   }
}
