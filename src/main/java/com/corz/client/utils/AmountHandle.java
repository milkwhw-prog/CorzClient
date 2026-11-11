package com.corz.client.utils;

import com.corz.client.render.CorzRoundedUtils;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;

public final class AmountHandle {
   private static final int UNUSED_ONE = 1;
   private final int argbColor;
   private float maxRadius;
   private final float framebufferScale;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private final float centerX;
   private final List<AmountHandle.Prim> panelPrimList = new ArrayList<>();
   private final int panelValue;
   private final float panelAmount;
   private final float centerY;
   private final boolean panelActive;
   private final boolean handleActive;
   private final float halfBboxAmount;

   public void handleAmountFlag(float degrees, float handleValue, float handleValue2, float dotRingBound) {
      int round = Math.round(degrees / 90.0F);
      if (Math.abs(degrees - round * 90.0F) < 0.01F) {
         float degScaled = dotRingBound / 2.0F;
         switch ((round % 4 + 4) % 4) {
            case 0:
               this.handleAmountFlagForFloat(handleValue, -degScaled, handleValue2, degScaled);
               break;
            case 1:
               this.handleAmountFlagForFloat(-degScaled, handleValue, degScaled, handleValue2);
               break;
            case 2:
               this.handleAmountFlagForFloat(-handleValue2, -degScaled, -handleValue, degScaled);
               break;
            default:
               this.handleAmountFlagForFloat(-degScaled, -handleValue2, degScaled, -handleValue);
         }
      } else {
         double angle = Math.toRadians(degrees);
         float handleFactor = (handleValue + handleValue2) / 2.0F;
         float handleBound = (handleValue2 - handleValue) / 2.0F / this.framebufferScale;
         float hashHuntBound = dotRingBound / 2.0F / this.framebufferScale;
         this.handleAmountMax(
            0,
            this.centerX + (float)Math.cos(angle) * handleFactor / this.framebufferScale,
            this.centerY + (float)Math.sin(angle) * handleFactor / this.framebufferScale,
            handleBound,
            hashHuntBound,
            this.handleActive ? Math.min(handleBound, hashHuntBound) : 0.0F,
            (float)angle,
            0.0F,
            0.0F
         );
      }
   }

   public void handleAmount(float handleValue) {
      float handleBound = handleValue / this.framebufferScale;
      this.handleAmountMax(0, this.centerX, this.centerY, handleBound, handleBound, handleBound, 0.0F, 0.0F, 0.0F);
   }

   public void updateAmountPanel() {
      if (!this.panelActive && !this.panelPrimList.isEmpty()) {
         float panelOverlayAmount = CorzRoundedUtils.panelOverlayAmount;
         float panelValue = (this.panelValue >> 16 & 0xFF) / 255.0F;
         float panelValue2 = (this.panelValue >> 8 & 0xFF) / 255.0F;
         float panelValue3 = (this.panelValue & 0xFF) / 255.0F;
         float panelValue4 = (this.panelValue >>> 24 & 0xFF) / 255.0F * panelOverlayAmount;
         float panelValue5 = (this.argbColor >> 16 & 0xFF) / 255.0F;
         float panelValue6 = (this.argbColor >> 8 & 0xFF) / 255.0F;
         float panelValue7 = (this.argbColor & 0xFF) / 255.0F;
         float panelSamplerValue = (this.argbColor >>> 24 & 0xFF) / 255.0F * panelOverlayAmount;
         if (this.halfBboxAmount > 0.0F || this.panelAmount > 0.0F) {
            for (AmountHandle.Prim prim : this.panelPrimList) {
               this.handleHalfBbox(prim, 1, panelValue, panelValue2, panelValue3, panelValue4, panelValue5, panelValue6, panelValue7, panelSamplerValue);
            }
         }

         for (AmountHandle.Prim prim2 : this.panelPrimList) {
            this.handleHalfBbox(prim2, 2, panelValue, panelValue2, panelValue3, panelValue4, panelValue5, panelValue6, panelValue7, panelSamplerValue);
         }

         this.panelPrimList.clear();
      }
   }

   public float getMax() {
      return this.maxRadius;
   }

   public void handleAmount(float handleValue, float strokeWidth, float degrees, float handleValue3) {
      float glfwLimit = handleValue / this.framebufferScale;
      float handleValue5 = strokeWidth / 2.0F / this.framebufferScale;
      int round = Math.round(degrees / 90.0F);
      if (Math.abs(degrees - round * 90.0F) < 0.01F) {
         float max7 = Math.max(1.0F, (float)Math.round(handleValue5 * 2.0F));
         handleValue5 = max7 / 2.0F;
         glfwLimit = max7 % 2.0F == 0.0F ? Math.round(glfwLimit) : (float)Math.floor(glfwLimit) + 0.5F;
         degrees = 0.0F;
      }

      this.handleAmountMax(0, this.centerX, this.centerY, glfwLimit, glfwLimit, handleValue3 / this.framebufferScale, (float)Math.toRadians(degrees), handleValue5, 0.0F);
   }

   public void handleAmountForFloat(float handleValue, float strokeWidth, float handleValue3, float handleLimit) {
      float handleBound = (handleValue + strokeWidth / 2.0F) / this.framebufferScale;
      float max5 = Math.max(0.0F, (handleValue - strokeWidth / 2.0F) / this.framebufferScale);
      float handleValue6 = handleLimit >= 360.0F ? 10.0F : (float)Math.toRadians(handleLimit / 2.0F);
      this.handleAmountMax(1, this.centerX, this.centerY, handleBound, handleBound, 0.0F, (float)Math.toRadians(handleValue3 - 90.0F), max5, handleValue6);
   }

   public void handleAmount(float degrees, float handleFactor, float handleValue) {
      double angle = Math.toRadians(degrees);
      this.handleAmountMax(
         2,
         this.centerX + (float)Math.cos(angle) * handleFactor / this.framebufferScale,
         this.centerY + (float)Math.sin(angle) * handleFactor / this.framebufferScale,
         handleValue / this.framebufferScale,
         0.0F,
         0.0F,
         (float)Math.toRadians(degrees + 90.0F),
         0.0F,
         0.0F
      );
   }

   private void handleHalfBbox(AmountHandle.Prim prim, int halfBboxValue, float halfBboxValue2, float halfBboxValue3, float halfBboxValue4, float halfBboxValue5, float halfBboxValue6, float halfBboxValue7, float halfBboxValue8, float panelSamplerValue) {
      float crosshairFramebufferValue = (prim.cx - prim.bboxHalf) * this.framebufferScale;
      float peekPositionLimit = (prim.cx + prim.bboxHalf) * this.framebufferScale;
      float matrixStackValue = (prim.cy - prim.bboxHalf) * this.framebufferScale;
      float halfBboxValue13 = (prim.cy + prim.bboxHalf) * this.framebufferScale;
      CorzRoundedUtils.handleCrosshairFramebuffer(
         MATRIX4F,
         crosshairFramebufferValue,
         matrixStackValue,
         peekPositionLimit,
         halfBboxValue13,
         prim.shape,
         halfBboxValue,
         prim.cx,
         prim.cy,
         prim.halfW,
         prim.halfH,
         prim.radius,
         prim.angle,
         prim.inner,
         prim.arcHalf,
         this.halfBboxAmount,
         this.panelAmount,
         halfBboxValue2,
         halfBboxValue3,
         halfBboxValue4,
         halfBboxValue5,
         halfBboxValue6,
         halfBboxValue7,
         halfBboxValue8,
         panelSamplerValue
      );
   }

   private float getAmount(float handleValue) {
      float planValuesItem = handleValue / this.framebufferScale;
      return Math.signum(planValuesItem) * (float)Math.floor(Math.abs(planValuesItem) + 0.5F);
   }

   public void handle(float handleValue, float strokeWidth) {
      this.handleAmountForFloat(handleValue, strokeWidth, 0.0F, 360.0F);
   }

   static int getValue(int handleValue) {
      float handleValue2 = (handleValue >> 16 & 0xFF) / 255.0F;
      float handleValue3 = (handleValue >> 8 & 0xFF) / 255.0F;
      float handleValue4 = (handleValue & 0xFF) / 255.0F;
      float handleLimit = 0.2126F * handleValue2 + 0.7152F * handleValue3 + 0.0722F * handleValue4;
      int round = Math.round((handleValue >>> 24 & 0xFF) * 0.8F);
      return handleLimit > 0.45F ? round << 24 : round << 24 | 16777215;
   }

   private void handleAmountMax(int handleValue, float handleValue2, float handleValue3, float handleValue4, float handleBound, float handleValue6, float handleValue7, float handleValue8, float handleValue9) {
      float handleValue10 = switch (handleValue) {
         case 1 -> handleValue4;
         case 2 -> handleValue4 * 1.1547F;
         default -> (float)Math.hypot(handleValue4, handleBound);
      };
      float handleValue11 = this.halfBboxAmount + this.panelAmount;
      this.maxRadius = Math.max(this.maxRadius, ((float)Math.hypot(handleValue2 - this.centerX, handleValue3 - this.centerY) + handleValue10 + handleValue11) * this.framebufferScale);
      if (!this.panelActive) {
         this.panelPrimList.add(new AmountHandle.Prim(handleValue, handleValue2, handleValue3, handleValue4, handleBound, handleValue6, handleValue7, handleValue8, handleValue9, handleValue10 + handleValue11 + 1.5F));
      }
   }

   public void handleAmountFlagForFloat(float handleBound, float handleBound2, float degScaled, float handleBound4) {
      float handleValue = this.getAmount(Math.min(handleBound, degScaled));
      float handleValue2 = this.getAmount(Math.max(handleBound, degScaled));
      float handleValue3 = this.getAmount(Math.min(handleBound2, handleBound4));
      float handleValue4 = this.getAmount(Math.max(handleBound2, handleBound4));
      if (handleValue2 - handleValue < 1.0F) {
         handleValue2 = handleValue + 1.0F;
      }

      if (handleValue4 - handleValue3 < 1.0F) {
         handleValue4 = handleValue3 + 1.0F;
      }

      float handleBound5 = (handleValue2 - handleValue) / 2.0F;
      float handleBound6 = (handleValue4 - handleValue3) / 2.0F;
      this.handleAmountMax(
         0, this.centerX + (handleValue + handleValue2) / 2.0F, this.centerY + (handleValue3 + handleValue4) / 2.0F, handleBound5, handleBound6, this.handleActive ? Math.min(handleBound5, handleBound6) : 0.0F, 0.0F, 0.0F, 0.0F
      );
   }

   public void handleAmountFlag(float handleValue, float handleValue2, float handleValue3, float handleValue4, float dotRingBound) {
      float x = handleValue3 - handleValue;
      float y = handleValue4 - handleValue2;
      float hypot = (float)Math.hypot(x, y);
      if (!(hypot <= 0.001F)) {
         float handleBound = hypot / 2.0F / this.framebufferScale;
         float handleBound2 = dotRingBound / 2.0F / this.framebufferScale;
         this.handleAmountMax(
            0,
            this.centerX + (handleValue + handleValue3) / 2.0F / this.framebufferScale,
            this.centerY + (handleValue2 + handleValue4) / 2.0F / this.framebufferScale,
            handleBound,
            handleBound2,
            this.handleActive ? Math.min(handleBound, handleBound2) : 0.0F,
            (float)Math.atan2(y, x),
            0.0F,
            0.0F
         );
      }
   }

   public AmountHandle(float handleValue, float handleValue2, int value, boolean handleFlag, boolean handleFlag2, boolean flag, boolean flag2) {
      this.panelActive = flag2;
      this.framebufferScale = flag2 ? 1.0F : CorzRoundedUtils.getFramebufferHeight();
      this.centerX = handleValue / this.framebufferScale;
      this.centerY = handleValue2 / this.framebufferScale;
      this.panelValue = value;
      this.argbColor = handleFlag ? getValue(value) : 0;
      this.halfBboxAmount = handleFlag ? Math.max(1.0F, 0.55F / this.framebufferScale) : 0.0F;
      this.panelAmount = handleFlag2 ? 2.5F / this.framebufferScale : 0.0F;
      this.handleActive = flag;
   }

   private record Prim(int shape, float cx, float cy, float halfW, float halfH, float radius, float angle, float inner, float arcHalf, float bboxHalf) {
   }
}
