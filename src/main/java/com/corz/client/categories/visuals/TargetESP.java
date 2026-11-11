package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.entity.EntityEyeUtils;
import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.TexBuilderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class TargetESP extends Module {
   private final BooleanSetting booleanBase;
   private static final Identifier identifier = Identifier.of("corzclient", "textures/world/targetesp/target.png");
   private final DoubleSetting doubleHelper;
   private final DoubleSetting spinSpeed;
   private long timeMillis;
   private final BooleanSetting playersOnly;
   private static final Identifier identifierAimbotIdentifier = Identifier.of("corzclient", "textures/world/targetesp/targetn.png");
   private final DoubleSetting range;
   private final BooleanSetting customColor;
   private static final Identifier targetEspIdentifier = Identifier.of("corzclient", "textures/world/targetesp/nur.png");
   private BufferAllocator bufferAllocator;
   private final DoubleSetting pulseSpeed;
   private Immediate immediate;
   private final BooleanSetting pulse;
   private final EnumSetting enumHelper = new EnumSetting("Style", "Which marker texture to draw", "Target", "Target", "Ring", "Nurik", "Delta", "Aimbot");
   private static final Identifier DELTA_TEXTURE = Identifier.of("corzclient", "textures/world/targetesp/target1.png");
   private LivingEntity livingEntity;
   private final ColorSetting colorBase;
   private static final Identifier AIMBOT_TEXTURE = Identifier.of("corzclient", "textures/world/targetesp/aimbot.png");
   private final DoubleSetting brightness;
   private float fadeProgress;

   private LivingEntity getPlayer() {
      if (ClampMatrixUtils.isInstanceScreen()) {
         return mc.player;
      } else {
         if (this.livingEntity == mc.player) {
            this.livingEntity = null;
            this.fadeProgress = 0.0F;
         }

         double playerValue = this.range.getValue();
         return (LivingEntity)(this.playersOnly.isEnabled()
            ? EntityEyeUtils.getNext(playerValue)
            : EntityEyeUtils.getInstanceEye(playerValue, entity -> !(entity instanceof PlayerEntity var1x) ? true : !var1x.isSpectator() && !FriendNameStore.canFlag(var1x)));
      }
   }

   private Identifier getIdentifierAimbot() {
      String formatText = this.enumHelper.getCurrentOption();

      return switch (formatText) {
         case "Ring" -> identifierAimbotIdentifier;
         case "Nurik" -> targetEspIdentifier;
         case "Delta" -> DELTA_TEXTURE;
         case "Aimbot" -> AIMBOT_TEXTURE;
         default -> identifier;
      };
   }

   @Override
   public void onDisable() {
      this.fadeProgress = 0.0F;
      this.livingEntity = null;
      this.timeMillis = 0L;
      if (this.bufferAllocator != null) {
         this.bufferAllocator.close();
         this.bufferAllocator = null;
         this.immediate = null;
      }
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      if (this.isEnabled() && mc.player != null && mc.world != null) {
         LivingEntity livingEntity = this.getPlayer();
         if (livingEntity != null) {
            this.livingEntity = livingEntity;
         }

         long timeMillis = System.currentTimeMillis();
         float targetEspValue = this.timeMillis == 0L ? 0.0F : (float)(timeMillis - this.timeMillis) / 300.0F;
         this.timeMillis = timeMillis;
         float targetEspBound = livingEntity != null ? 1.0F : 0.0F;
         if (this.fadeProgress < targetEspBound) {
            this.fadeProgress = Math.min(targetEspBound, this.fadeProgress + targetEspValue);
         } else if (this.fadeProgress > targetEspBound) {
            this.fadeProgress = Math.max(targetEspBound, this.fadeProgress - targetEspValue);
         }

         LivingEntity livingEntity2 = livingEntity != null ? livingEntity : this.livingEntity;
         if (livingEntity2 != null && !(this.fadeProgress <= 0.01F)) {
            float targetEspFactor = this.fadeProgress * this.fadeProgress * (3.0F - 2.0F * this.fadeProgress);
            if (this.bufferAllocator == null) {
               this.bufferAllocator = new BufferAllocator(524288);
               this.immediate = VertexConsumerProvider.immediate(this.bufferAllocator);
            }

            RenderLayer apply = TexBuilderUtils.texBuilderFunction.apply(this.getIdentifierAimbot());
            VertexConsumer buffer = this.immediate.getBuffer(apply);
            MatrixStack matrices = queueCommand.matrices;
            Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
            float tickProgress = mc.getRenderTickCounter().getTickProgress(true);
            Vec3d lerpedPos = livingEntity2.getLerpedPos(tickProgress);
            int targetEspValue2 = this.customColor.isEnabled() ? this.colorBase.getColor() : MatrixEspUtils.releaseRightValue;
            int targetEspValue3 = this.customColor.isEnabled() ? this.colorBase.getColor() : MatrixEspUtils.releaseRightAccentColor;
            float targetEspValue4 = (float)this.brightness.getValue();
            float targetEspFactor2 = (0.7F + (1.0F - targetEspFactor) * 0.5F) * (float)this.doubleHelper.getValue();
            float targetEspFactor3 = this.pulse.isEnabled() ? 0.85F + 0.15F * (float)Math.sin(timeMillis / 1000.0 * this.pulseSpeed.getValue() * Math.PI) : 1.0F;
            float targetEspValue5 = (float)Math.sin(timeMillis / 1000.0) * (float)this.spinSpeed.getValue();
            int targetEspLimit = this.booleanBase.isEnabled() ? 2 : 1;

            for (int i = 0; i < targetEspLimit; i++) {
               float targetEspFactor4 = i == 0 ? 1.0F : 0.5F;
               float x = targetEspFactor2 * (i == 0 ? 1.0F : 1.05F) * targetEspFactor3;
               int targetEspValue6 = getValue(targetEspValue2, targetEspValue4, targetEspFactor * targetEspFactor4);
               int targetEspValue7 = getValue(targetEspValue3, targetEspValue4, targetEspFactor * targetEspFactor4);
               matrices.push();
               matrices.translate(lerpedPos.x - cameraPos.x, lerpedPos.y - cameraPos.y + livingEntity2.getHeight() / 2.0F, lerpedPos.z - cameraPos.z);
               matrices.multiply(mc.gameRenderer.getCamera().getRotation());
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(targetEspValue5));
               matrices.scale(x, x, 1.0F);
               Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
               buffer.vertex(positionMatrix, -1.0F, -1.0F, 0.0F).texture(0.0F, 0.0F).color(targetEspValue7);
               buffer.vertex(positionMatrix, -1.0F, 1.0F, 0.0F).texture(0.0F, 1.0F).color(targetEspValue6);
               buffer.vertex(positionMatrix, 1.0F, 1.0F, 0.0F).texture(1.0F, 1.0F).color(targetEspValue7);
               buffer.vertex(positionMatrix, 1.0F, -1.0F, 0.0F).texture(1.0F, 0.0F).color(targetEspValue6);
               matrices.pop();
            }

            this.immediate.draw();
         } else {
            if (this.fadeProgress <= 0.01F) {
               this.livingEntity = null;
            }
         }
      }
   }

   private static int getValue(int targetEspValue, float targetEspFactor, float targetEspValue2) {
      int min3 = Math.min(255, (int)((targetEspValue >> 16 & 0xFF) * targetEspFactor));
      int min4 = Math.min(255, (int)((targetEspValue >> 8 & 0xFF) * targetEspFactor));
      int min5 = Math.min(255, (int)((targetEspValue & 0xFF) * targetEspFactor));
      int max6 = Math.max(0, Math.min(255, (int)(targetEspValue2 * 255.0F)));
      return max6 << 24 | min3 << 16 | min4 << 8 | min5;
   }

   public TargetESP() {
      super("Target ESP", IconsPngType.VISUAL);
      this.range = new DoubleSetting("Range", "Max distance to pick a target", 8.0, 3.0, 30.0, 0.5);
      this.playersOnly = new BooleanSetting("Players Only", "Only mark players (never mobs)", true);
      this.doubleHelper = new DoubleSetting("Size", "Marker size", 1.0, 0.3, 3.0, 0.1);
      this.spinSpeed = new DoubleSetting("Spin Speed", "How fast the marker wobble-spins", 180.0, 0.0, 360.0, 10.0);
      this.brightness = new DoubleSetting("Brightness", "Colour brightness boost", 2.0, 0.5, 3.0, 0.1);
      this.booleanBase = new BooleanSetting("Glow", "Second additive pass for a soft bloom", true);
      this.pulse = new BooleanSetting("Pulse", "Gently pulse the marker size", true);
      this.pulseSpeed = new DoubleSetting("Pulse Speed", "Pulse rate", 2.0, 0.5, 5.0, 0.1);
      this.customColor = new BooleanSetting("Custom Color", "Use a fixed colour instead of the theme", false);
      this.colorBase = new ColorSetting("Color", "Marker colour when Custom Color is on", -16733441);
      this.addSettings(
         new ConfigSetting[]{
            this.enumHelper,
            this.range,
            this.playersOnly,
            this.doubleHelper,
            this.spinSpeed,
            this.brightness,
            this.booleanBase,
            this.pulse,
            this.pulseSpeed,
            this.customColor,
            this.colorBase
         }
      );
   }
}
