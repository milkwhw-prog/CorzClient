package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.BlockWaveRenderer;
import com.corz.client.utils.JumpCircleRules;
import com.corz.client.utils.IconsPngType;

public class JumpCircles extends Module {
   private final BlockWaveRenderer blockWaveRenderer;
   private final ColorSetting colorBase;
   private final DoubleSetting doubleHelper;
   private int airTicks;
   private double x;
   private final DoubleSetting waveSpeed;
   private final BooleanSetting booleanBase;
   private final Deque<JumpCircles.Circle> deque;
   private final BooleanSetting crestGlow;
   private double y;
   private final DoubleSetting waveHeight;
   private final BooleanSetting particles;
   private final IntegerSetting integerHelper;
   private final DoubleSetting radiusPlayerVehicle;
   private final BooleanSetting rainbow;
   private final DoubleSetting jumpCirclesDoubleHelper;
   private final EnumSetting enumHelper = new EnumSetting("Style", "Ring, a ripple of real blocks, or both", "Ring", "Ring", "Block Wave", "Both");
   private double iIiAmount;
   private double z;
   private boolean onGround;
   private final EnumSetting trigger = new EnumSetting("Trigger", "Spawn when you jump, when you land, or both", "Jump", "Jump", "Land", "Both");
   private int playerDequeCount;

   private void handleAmount(Matrix4f matrix4f, JumpCircles.Circle circle, long jumpCirclesValue, float jumpCirclesValue2, float jumpCirclesValue3, int jumpCirclesValue4) {
      float jumpCirclesLimit = circle.getTime(jumpCirclesValue);
      float jumpCirclesFactor;
      float amountFloat2;
      if (jumpCirclesLimit < 0.25F) {
         float jumpCirclesValue5 = jumpCirclesLimit / 0.25F;
         jumpCirclesFactor = getAmount(jumpCirclesValue5);
         amountFloat2 = getAmountFloat(Math.min(1.0F, jumpCirclesValue5 * 2.0F));
      } else if (jumpCirclesLimit > jumpCirclesValue2 - 0.4F) {
         float jumpCirclesValue6 = (jumpCirclesLimit - (jumpCirclesValue2 - 0.4F)) / 0.4F;
         jumpCirclesFactor = getAmount(getAmountFloat(jumpCirclesValue6), 1.0F, 1.3F);
         amountFloat2 = 1.0F - getAmountForFloat(jumpCirclesValue6);
      } else {
         jumpCirclesFactor = 1.0F;
         amountFloat2 = 1.0F;
      }

      float jumpCirclesValue7 = 0.5F + 0.5F * (float)Math.sin(jumpCirclesLimit * 10.0F);
      int jumpCirclesValue8 = getValue(jumpCirclesValue4, 16756958, jumpCirclesValue7 * 0.35F);
      int playerValue = getValue(jumpCirclesValue8, amountFloat2 * (0.82F + 0.18F * jumpCirclesValue7));
      float jumpCirclesValue10 = 0.5F * jumpCirclesValue3 * jumpCirclesFactor;
      this.handle(matrix4f, circle.posX, circle.circleAmount + circle.heightOffset, circle.posZ, jumpCirclesValue10, playerValue, 2.0F);
      this.handle(matrix4f, circle.posX, circle.circleAmount + circle.heightOffset, circle.posZ, jumpCirclesValue10 * 0.6F, getValue(jumpCirclesValue8, amountFloat2 * 0.4F * (0.82F + 0.18F * jumpCirclesValue7)), 1.5F);
   }

   public JumpCircles() {
      super("Jump Circles", IconsPngType.VISUAL);
      this.jumpCirclesDoubleHelper = new DoubleSetting("Size", "Ring scale (1.0 = one block wide)", 1.0, 0.5, 3.0, 0.1);
      this.colorBase = new ColorSetting("Color", "Ring colour (alpha is animated)", -5204993);
      this.doubleHelper = new DoubleSetting("Lifetime", "How long a ring stays visible (seconds)", 1.5, 0.5, 4.0, 0.1);
      this.rainbow = new BooleanSetting("Rainbow", "Cycle the colour through the rainbow", false);
      this.booleanBase = new BooleanSetting("Shockwave", "Expanding ring on spawn", true);
      this.particles = new BooleanSetting("Particles", "Drifting dust motes when a circle or wave spawns", true);
      this.integerHelper = new IntegerSetting("Max", "Max simultaneous rings and waves (oldest goes first)", 10, 1, 30);
      this.waveHeight = new DoubleSetting("Wave Height", "How high the blocks rise at the crest (blocks)", 0.45, 0.1, 1.0, 0.05);
      this.waveSpeed = new DoubleSetting("Wave Speed", "How fast the crest travels outward (blocks per second)", 9.0, 2.0, 20.0, 0.5);
      this.radiusPlayerVehicle = new DoubleSetting("Radius", "How far the block wave reaches (blocks)", 6.0, 2.0, 10.0, 0.5);
      this.crestGlow = new BooleanSetting("Crest Glow", "Tint and glow the rising blocks in the ring colour", true);
      this.deque = new ArrayDeque<>();
      this.blockWaveRenderer = new BlockWaveRenderer();
      this.onGround = true;
      this.x = Double.NaN;
      this.iIiAmount = Double.NaN;
      this.z = Double.NaN;
      this.waveHeight.withGroupName("Block Wave");
      this.waveSpeed.withGroupName("Block Wave");
      this.radiusPlayerVehicle.withGroupName("Block Wave");
      this.crestGlow.withGroupName("Block Wave");
      this.addSettings(
         new ConfigSetting[]{
            this.enumHelper,
            this.trigger,
            this.jumpCirclesDoubleHelper,
            this.colorBase,
            this.doubleHelper,
            this.rainbow,
            this.booleanBase,
            this.particles,
            this.integerHelper,
            this.waveHeight,
            this.waveSpeed,
            this.radiusPlayerVehicle,
            this.crestGlow
         }
      );
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.player != null && mc.world != null) {
         if (!this.blockWaveRenderer.isEmpty()) {
            this.handleWaveRenderer(queueCommand, System.nanoTime());
         }

         if (!this.deque.isEmpty()) {
            long nanoTime = System.nanoTime();
            float jumpCirclesValue = Math.max(0.1F, (float)this.doubleHelper.getValue());
            var iterator = this.deque.iterator();

            while (iterator.hasNext()) {
               if (((JumpCircles.Circle)iterator.next()).getTime(nanoTime) > jumpCirclesValue) {
                  iterator.remove();
               }
            }

            if (!this.deque.isEmpty()) {
               Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
               MatrixStack matrices = queueCommand.matrices;
               matrices.push();
               matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
               Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
               float jumpCirclesFactor = (float)this.jumpCirclesDoubleHelper.getValue();
               int hsvRgb = this.getHsvRgb();

               for (JumpCircles.Circle circle : this.deque) {
                  this.handleAmount(positionMatrix, circle, nanoTime, jumpCirclesValue, jumpCirclesFactor, hsvRgb);
                  if (this.booleanBase.isEnabled()) {
                     this.handleAmount(positionMatrix, circle, nanoTime, jumpCirclesFactor, hsvRgb);
                  }
               }

               matrices.pop();
               VertexBuilderUtils.updateDrawImmediate();
            }
         }
      }
   }

   private void updatePlayerParticle() {
      int colorBase = this.getColorBase();
      Random random = mc.player.getRandom();

      for (int i = 0; i < 10; i++) {
         double angle = random.nextDouble() * Math.PI * 2.0;
         double playerParticleFactor = 0.15 + random.nextDouble() * 0.45;
         mc.world
            .addParticleClient(
               new DustParticleEffect(colorBase, 0.9F),
               mc.player.getX() + Math.cos(angle) * playerParticleFactor,
               mc.player.getY() + 0.05,
               mc.player.getZ() + Math.sin(angle) * playerParticleFactor,
               0.0,
               0.6 + random.nextDouble() * 0.4,
               0.0
            );
      }
   }

   private int getColorBase() {
      return this.colorBase.getColor() & 16777215;
   }

   private static float getAmount(float jumpCirclesValue) {
      jumpCirclesValue = getMin(jumpCirclesValue);
      float jumpCirclesFactor = 3.2F;
      float jumpCirclesFactor2 = jumpCirclesValue - 1.0F;
      return 1.0F + jumpCirclesFactor * jumpCirclesFactor2 * jumpCirclesFactor2 * jumpCirclesFactor2 + 2.2F * jumpCirclesFactor2 * jumpCirclesFactor2;
   }

   private void handleWaveRenderer(QueueCommand queueCommand, long waveRendererValue) {
      if (mc.world != this.blockWaveRenderer.getWorld()) {
         this.blockWaveRenderer.updateClearDeque();
      } else {
         Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
         float waveRendererValue2 = (float)(System.currentTimeMillis() % 4000L) / 4000.0F;
         this.blockWaveRenderer
            .handleAmountFlag(
               queueCommand.matrices,
               cameraPos,
               waveRendererValue,
               this.waveHeight.getValue(),
               this.waveSpeed.getValue(),
               this.getMax(),
               this.crestGlow.isEnabled(),
               this.getColorBase(),
               this.rainbow.isEnabled(),
               waveRendererValue2
            );
      }
   }

   private static float getMin(float jumpCirclesLimit) {
      return jumpCirclesLimit < 0.0F ? 0.0F : (jumpCirclesLimit > 1.0F ? 1.0F : jumpCirclesLimit);
   }

   private void handlePlayerDeque(double playerDequeValue) {
      String formatText = this.enumHelper.getCurrentOption();
      if (JumpCircleRules.canWaveBlock(formatText)) {
         float playerDequeValue2 = 0.01F + this.playerDequeCount % 8 * 0.001F;
         this.playerDequeCount++;
         boolean playerDequeFlag = JumpCircleRules.canFlag(
            this.x, this.iIiAmount, this.z, mc.player.getX(), mc.player.getY(), mc.player.getZ()
         );
         this.deque
            .addLast(
               new JumpCircles.Circle(
                  playerDequeFlag ? this.x : mc.player.getX(),
                  playerDequeFlag ? this.iIiAmount : mc.player.getY(),
                  playerDequeFlag ? this.z : mc.player.getZ(),
                  mc.player.getYaw(),
                  playerDequeValue2,
                  System.nanoTime()
               )
            );
         int max3 = Math.max(1, this.integerHelper.getValue());

         while (this.deque.size() > max3) {
            this.deque.removeFirst();
         }
      }

      if (JumpCircleRules.canBothWave(formatText)) {
         this.handlePlayerVehicle(playerDequeValue);
      }

      if (this.particles.isEnabled()) {
         this.updatePlayerParticle();
      }
   }

   private void handleAmount(Matrix4f matrix4f, JumpCircles.Circle circle, long jumpCirclesValue, float jumpCirclesFactor, int hsvRgb) {
      float jumpCirclesLimit = circle.getTime(jumpCirclesValue);
      if (!(jumpCirclesLimit >= 0.45F)) {
         float jumpCirclesValue3 = jumpCirclesLimit / 0.45F;
         float jumpCirclesValue4 = getAmount(getAmountFloat(jumpCirclesValue3), 0.35F, 2.2F) * jumpCirclesFactor;
         float jumpCirclesValue5 = 0.85F * (1.0F - getAmountForFloat(jumpCirclesValue3));
         this.handle(matrix4f, circle.posX, circle.circleAmount + circle.heightOffset * 0.5F + 0.004, circle.posZ, jumpCirclesValue4, getValue(hsvRgb, jumpCirclesValue5), 1.5F);
      }
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null) {
         if (!JumpCircleRules.canBothWave(this.enumHelper.getCurrentOption())) {
            if (!this.blockWaveRenderer.isEmpty()) {
               this.blockWaveRenderer.updateClearDeque();
            }
         } else {
            this.blockWaveRenderer.handleDequeFlag(mc.world, System.nanoTime(), this.waveSpeed.getValue(), this.getMax());
         }

         boolean onGround = mc.player.isOnGround();
         double y = mc.player.getVelocity().y;
         if (onGround) {
            this.x = mc.player.getX();
            this.iIiAmount = mc.player.getY();
            this.z = mc.player.getZ();
         }

         boolean iIiFlag = JumpCircleRules.canFlag(this.onGround, onGround, y);
         boolean canFlag = JumpCircleRules.canFlag(this.onGround, onGround, this.airTicks);
         double iIiValue = canFlag ? JumpCircleRules.getAmount(this.y) : 1.0;
         if (!onGround) {
            this.y = y;
         }

         this.airTicks = onGround ? 0 : this.airTicks + 1;
         if (JumpCircleRules.canLandBoth(this.trigger.getCurrentOption(), iIiFlag, canFlag)) {
            this.handlePlayerDeque(iIiValue);
         }

         this.onGround = onGround;
      } else {
         this.blockWaveRenderer.updateClearDeque();
      }
   }

   private static int getValue(int jumpCirclesValue, float jumpCirclesLimit) {
      int jumpCirclesValue3 = (int)(getMin(jumpCirclesLimit) * 255.0F);
      return jumpCirclesValue3 << 24 | jumpCirclesValue & 16777215;
   }

   private double getMax() {
      return Math.max(0.1, this.doubleHelper.getValue());
   }

   private static float getAmount(float jumpCirclesFactor, float jumpCirclesValue, float jumpCirclesValue2) {
      return jumpCirclesValue + (jumpCirclesValue2 - jumpCirclesValue) * jumpCirclesFactor;
   }

   @Override
   protected void onDisable() {
      this.deque.clear();
      this.blockWaveRenderer.updateClearDeque();
      this.onGround = true;
      this.airTicks = 0;
      this.x = this.iIiAmount = this.z = Double.NaN;
      this.y = 0.0;
   }

   private static float getAmountForFloat(float jumpCirclesFactor) {
      jumpCirclesFactor = getMin(jumpCirclesFactor);
      return jumpCirclesFactor * jumpCirclesFactor;
   }

   private int getHsvRgb() {
      if (this.rainbow.isEnabled()) {
         float hsvRgbValue = (float)(System.currentTimeMillis() % 4000L) / 4000.0F;
         return MathHelper.hsvToRgb(hsvRgbValue, 0.75F, 1.0F) & 16777215;
      } else {
         return this.getColorBase();
      }
   }

   private void handlePlayerVehicle(double playerVehicleValue) {
      if (!mc.player.isSpectator()
         && !mc.player.isTouchingWater()
         && !mc.player.isInLava()
         && !mc.player.hasVehicle()) {
         if (JumpCircleRules.canFlag(this.x, this.iIiAmount, this.z, mc.player.getX(), mc.player.getY(), mc.player.getZ())) {
            this.blockWaveRenderer
               .canStatesFloor(mc.world, this.x, this.iIiAmount, this.z, this.radiusPlayerVehicle.getValue(), this.integerHelper.getValue(), playerVehicleValue, System.nanoTime());
         }
      }
   }

   private void handle(Matrix4f matrix4f, double jumpCirclesValue, double jumpCirclesValue2, double jumpCirclesValue3, float jumpCirclesFactor, int playerValue, float z22) {
      float vertexBuilderValue = 0.0F;
      float amountForDouble = 0.0F;

      for (int i = 0; i <= 40; i++) {
         double angle = i / 40.0 * Math.PI * 2.0;
         float amountX = (float)(jumpCirclesValue + Math.cos(angle) * jumpCirclesFactor);
         float jumpCirclesValue9 = (float)(jumpCirclesValue3 + Math.sin(angle) * jumpCirclesFactor);
         if (i > 0) {
            VertexBuilderUtils.handleForMatrix4fFloat(matrix4f, vertexBuilderValue, (float)jumpCirclesValue2, amountForDouble, amountX, (float)jumpCirclesValue2, jumpCirclesValue9, playerValue, z22);
         }

         vertexBuilderValue = amountX;
         amountForDouble = jumpCirclesValue9;
      }
   }

   private static float getAmountFloat(float jumpCirclesValue) {
      jumpCirclesValue = getMin(jumpCirclesValue);
      float jumpCirclesFactor = 1.0F - jumpCirclesValue;
      return 1.0F - jumpCirclesFactor * jumpCirclesFactor * jumpCirclesFactor;
   }

   private static int getValue(int jumpCirclesValue, int jumpCirclesValue2, float jumpCirclesFactor) {
      int jumpCirclesValue4 = (int)getAmount(jumpCirclesFactor, jumpCirclesValue >> 16 & 0xFF, jumpCirclesValue2 >> 16 & 0xFF);
      int jumpCirclesValue5 = (int)getAmount(jumpCirclesFactor, jumpCirclesValue >> 8 & 0xFF, jumpCirclesValue2 >> 8 & 0xFF);
      int jumpCirclesValue6 = (int)getAmount(jumpCirclesFactor, jumpCirclesValue & 0xFF, jumpCirclesValue2 & 0xFF);
      return jumpCirclesValue4 << 16 | jumpCirclesValue5 << 8 | jumpCirclesValue6;
   }

   private static final class Circle {
      final double posZ;
      final double circleAmount;
      final float maxRadius;
      final double posX;
      final long createdNanos;
      final float heightOffset;

      float getTime(long circleValue) {
         return (float)(circleValue - this.createdNanos) / 1.0E9F;
      }

      Circle(double amount, double amount2, double amount3, float amount4, float amount5, long time) {
         this.posX = amount;
         this.circleAmount = amount2;
         this.posZ = amount3;
         this.maxRadius = amount4;
         this.heightOffset = amount5;
         this.createdNanos = time;
      }
   }
}
